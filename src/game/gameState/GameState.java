package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

public abstract class GameState {
	
        protected int nbMsgSyncState;
	protected Object stepDone;
	protected Game game;
	protected List<Player> playersReady;
	
	public GameState(Game game) {
		this.stepDone= new Object();
		this.game = game;
		this.playersReady = new Vector<>();
	}
	
	public abstract void receiveMessage(String from, Serializable msg) throws RemoteException;
	
	protected void ignoredMessage(String from, Serializable msg) {
		System.out.println("Message ignored : " + from + " : " + msg);
	}
	
	protected String getPlayerName() {
		return game.getPlayer().getName();
	}
	
	protected void notifyStepDone() {
		synchronized (stepDone) {
			stepDone.notify();
			
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
	
	public abstract void start();
	protected abstract void goToNextStep();
	
	
	public String toString() {
		return getClass().getSimpleName().replace("GameState", "");
	}
}
