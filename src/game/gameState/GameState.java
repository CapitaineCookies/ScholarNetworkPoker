package game.gameState;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.RemotePlayer;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import message.Message;
import reso.Client;
import reso.Reso;

public abstract class GameState extends GameStateVisitorImpl {

	public enum EGameState {

		A_getReso, B_declaration, C_getOthers, D_distribNumber, E_election, F_cardsDistribution, G_cardsTrade, H_cardsShow, Z_exit;

		@Override
		public String toString() {
			char firstChar = super.toString().charAt(2);
			return Character.toUpperCase(firstChar) + super.toString().substring(3);
		}
	}

	protected Semaphore preLock;
	protected Semaphore postLock;
	protected Semaphore stepDone;

	protected final Reso reso;
	protected final LocalPlayer localPlayer;
	protected final OtherPlayers otherPlayers;

	public GameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers) {
		this.reso = reso;
		this.localPlayer = localPlayer;
		this.otherPlayers = otherPlayers;

		this.preLock = new Semaphore(0);
		this.postLock = new Semaphore(0);
		this.stepDone = new Semaphore(0);
	}

	public void start() {
		log("[StartPreExecute]");
		preExecute();

		if (makePostPreExecuteSynchro()) {
			sendPostPreExecuteSynchro();
			waitPostPreExecuteSynchro();
		}

		log("[StartExecute]");
		execute();
		waitStepDone();

		if (makePrePostExecuteSynchro()) {
			sendPrePostExecuteSynchro();
			waitPrePostExecuteSynchro();
		}
		log("[StartPostExecute]");
		postExecute();
	}

	// ///////////////////////
	// Protocol
	// ///////////////////////

	/**
	 * Use of initializations. Use with preExecuteSynchro for more secure
	 * 
	 */
	protected void preExecute() {
		// By default, do nothing
	}

	/**
	 * Override if a synchronization is needed between preExecute and execute
	 * 
	 * @return true for synchronization
	 */
	protected boolean makePostPreExecuteSynchro() {
		return false;
	}

	protected abstract void sendPostPreExecuteSynchro();

	protected abstract void waitPostPreExecuteSynchro();

	/**
	 * Put here the Main execution of the gameState
	 */
	protected abstract void execute();

	/**
	 * Override if a synchronization is needed between preExecute and execute
	 * 
	 * @return true for synchronization
	 */
	protected boolean makePrePostExecuteSynchro() {
		return false;
	}

	protected abstract void sendPrePostExecuteSynchro();

	protected abstract void waitPrePostExecuteSynchro();

	/**
	 * Call after calls execute() AND notifyDone() Use of initializations. Use with postExecuteSynchro for more secure
	 * 
	 */
	protected void postExecute() {
		// By default, do nothing
	}

	/**
	 * 
	 * @return The enum for this gameState
	 */
	public abstract EGameState getGameState();

	/**
	 * 
	 * @return The enum of the next gameState
	 */
	public abstract EGameState getNextState();

	// /////////////////
	// Network
	// /////////////////

	public void send(String to, Message message) {
		log("[send]  to  [" + to + "]" + message);
		message.setSenderGameState(getGameState());
		try {
			reso.sendMessage(localPlayer.getName(), to, message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void send(Player to, Message msg) {
		send(to.getName(), msg);
	}

	public void sendToOthers(Message message) {
		message.setSenderGameState(getGameState());
		log("[Send]  to  " + otherPlayers.getPlayers() + message);
		for (Player player : otherPlayers.getPlayers()) {
			try {
				reso.sendMessage(localPlayer.getName(), player.getName(), message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void broadcast(Message message) {
		message.setSenderGameState(getGameState());
		log("[Brod] " + message);
		try {
			reso.broadcastMessage(localPlayer.getName(), message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// public void setReso(Reso reso) {
	// this.reso = reso;
	// }

	public void declarePlayer(Client client) {
		try {
			reso.declareClient(localPlayer.getName(), client);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void disconnectPlayer() {
		try {
			reso.removeClient(localPlayer.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// /////////////////
	// Players
	// /////////////////

	public void setOtherPlayer(Collection<RemotePlayer> otherPlayers) {
		this.otherPlayers.addAll(otherPlayers);
	}

	public Player getFrom(Message message) {
		return getPlayer(message.getFrom());
	}

	public boolean comeFromLocalPlayer(Message message) {
		return message.getFrom().equals(localPlayer.getName());
	}

	protected Player getPlayer(String name) {
		if (localPlayer.getName().equals(name))
			return localPlayer;
		Player needed = otherPlayers.getPlayer(name);
		if (needed != null)
			return needed;
		throw new RuntimeException("player : " + name + " unexist !");
	}

	// /////////////////
	// // Synchronized
	// /////////////////

	protected void notifyStepDone() {
		stepDone.release();
	}

	private void waitStepDone() {
		try {
			stepDone.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// /////////////////
	// // Tools
	// /////////////////

	public String toString() {
		String name = getClass().getSimpleName().replace("GameState", "");
		return name.substring(2);
	}

	public void log(String logMessage) {
		System.out.println("[" + localPlayer.getName() + "][" + getGameState() + "]" + logMessage);
	}

}