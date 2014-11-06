package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import message.MsgStepDone;

public abstract class GameState {

	public enum EGameState {

		declarePlayer, election, distribNumber, exit, getPlayers, getReso, cardsDistribution, cardsShow, cardsTrade,

	}

	protected int nbMsgSyncState;
	protected Object stepDone;
	protected Object stepOtherPlayersDone;
	protected Game game;
	protected List<Player> playersReady;

	private int nbMessageStepDone;

	public GameState(Game game) {
		this.stepDone = new Object();
		this.game = game;
		this.playersReady = new Vector<>();
		nbMessageStepDone = 0;
	}

	public abstract void receiveMessage(String from, Serializable msg) throws RemoteException;

	protected void ignoredMessage(String from, Serializable msg) {
		System.out.println("Message ignored : " + from + " : " + msg);
	}

	protected String getPlayerName() {
		return game.getPlayer().getName();
	}

	protected void sendMsgStepDone(String to, EGameState gameState) {
		String playerName = game.getPlayer().getName();
		try {
			game.sendMessage(playerName, to, new MsgStepDone(gameState));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void notifyStepDone() {
		synchronized (stepDone) {
			stepDone.notify();

		}
	}

	protected void notifyOtherPlayersDone() {
		synchronized (stepOtherPlayersDone) {
			stepOtherPlayersDone.notify();

		}
	}

	protected void waitStepDone() {
		synchronized (stepDone) {
			try {
				stepDone.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void receiveStepDone(String from) {
		nbMessageStepDone++;
		System.out.println("nbMessageStepDone=" + nbMessageStepDone + "/" + game.getOtherplayer().size());
		if (nbMessageStepDone == game.getOtherplayer().size()) {
			notifyOtherPlayersDone();
			System.out.println("Is notify");
		}

	}

	protected void waitOtherPlayersDone() {
		while (nbMessageStepDone < game.getOtherplayer().size()) {
			@SuppressWarnings("unused")
			int i = 0;
		}

		// if(nbMessageStepDone < game.getOtherplayer().size()) {
		// synchronized (stepOtherPlayersDone) {
		// try {
		// stepOtherPlayersDone.wait();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }
	}

	public abstract void start();

	protected abstract void goToNextStep();

	public String toString() {
		return getClass().getSimpleName().replace("GameState", "");
	}

	public abstract EGameState getEGameState();
}
