package game.gameState;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.RemotePlayer;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import message.Message;
import message.MessageVisitor;
import message.MsgPostSynch;
import message.MsgPreSynch;
import reso.Client;
import reso.Reso;

public abstract class GameState implements MessageVisitor {

	public enum EGameState {

		A_getReso, B_declaration, C_getOthers, D_distribNumber, E_election, F_cardsDistribution, G_cardsTrade, H_cardsShow, Z_exit;

		@Override
		public String toString() {
			char firstChar = super.toString().charAt(2);
			return Character.toUpperCase(firstChar) + super.toString().substring(3);
		}
	}

//	protected int nbMsgSyncState;
//	protected Object stepOtherPlayersDone;
//	// protected Game game;
//	protected List<Player> playersReady;

	protected Semaphore preLock;
	protected Semaphore postLock;
	protected Semaphore stepDone;

	protected final Reso reso;
	protected final LocalPlayer localPlayer;
	protected final OtherPlayers otherPlayers;
	protected final Player leader;
	public GameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers) {
		this(reso, localPlayer, otherPlayers, null);
	}
	
	public GameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		// this.playersReady = new Vector<>();
		// this.game = game;
		// nbMessageStepDone = 0;
		this.reso = reso;
		this.localPlayer = localPlayer;
		this.otherPlayers = otherPlayers;
		this.leader = leader;

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

	public void log(String logMessage) {
		System.out.println("[" + localPlayer.getName() + "][" + getGameState() + "]" + logMessage);
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

	private void sendPostPreExecuteSynchro() {
		sendToOthers(new MsgPreSynch());
	}

	private void waitPostPreExecuteSynchro() {

		if (otherPlayers == null)
			throw new RuntimeException("otherPlayers unset can't fix number of expected message");

		try {
			log("[WaitPreSynch] expected " + otherPlayers.size() + " message");
			preLock.acquire(otherPlayers.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

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

	private void sendPrePostExecuteSynchro() {
		sendToOthers(new MsgPostSynch());
	}

	private void waitPrePostExecuteSynchro() {

		if (otherPlayers == null)
			throw new RuntimeException("otherPlayers unset can't fix number of expected message");

		try {
			log("[WaitPostSynch] expected " + otherPlayers.size() + " message");
			postLock.acquire(otherPlayers.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Call after calls exectue() AND notifyDone()
	 * Use of initializations. Use with postExecuteSynchro for more secure
	 * 
	 */
	protected void postExecute() {
		// By default, do nothing
	}
	
	@Override
	public void receive(MsgPreSynch message) {
		preLock.release();
	}
	
	@Override
	public void receive(MsgPostSynch message) {
		postLock.release();
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
	// Reso
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

	public void sendToOthers(Message msg) {
		for (Player player : otherPlayers.getPlayers())
			send(player, msg);
	}

	public void broadcast(Message message) {
		message.setSenderGameState(getGameState());
		log("[Broadcast] " + message);
		try {
			reso.broadcastMessage(localPlayer.getName(), message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

//	public void setReso(Reso reso) {
//		this.reso = reso;
//	}

	public void declarePlayer(Client client) {
		try {
			reso.declareClient(localPlayer.getName(), client);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void disconnectPlayer() {
		try {
			reso.disconnect(localPlayer.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// /////////////////
	// Players
	// /////////////////
	
//	public void setLeader(Player leader) {
//		this.leader = leader;
//	}
	
	public boolean isLeader() {
		System.out.println("leader : " + leader);
		System.out.println("player : " + localPlayer);
		return leader.equals(localPlayer);
	}

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

	protected void ignoreMessage(Message message) {
		log(">>[Ignored] from [" + message.getFrom() + "]" + message);
	}
	
	public String toString() {
		String name = getClass().getSimpleName().replace("GameState", "");
		return name.substring(2);
	}

}