package game;

import game.gameState.ExitGameState;
import game.gameState.GameState;
import game.gameState.GetResoGameState;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import message.Message;
import reso.Client;
import reso.Reso;

public class Game extends UnicastRemoteObject implements Client, Runnable {

	private static final long serialVersionUID = -4064703456148532918L;

	protected Reso reso;
	protected Player player;
	protected GameState gameState;
	protected List<Player> otherPlayer;

	public Game(Player player) throws RemoteException {
		super();
		this.reso = null;
		this.otherPlayer = null;
		this.player = player;
		this.gameState = new GetResoGameState(this);
	}

	public Player getPlayer() {
		return player;
	}

	public List<Player> getOtherplayer() {
		return otherPlayer;
	}

	public void setReso(Reso reso) {
		this.reso = reso;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public void setOtherPlayer(List<Player> otherPlayers) {
		this.otherPlayer = otherPlayers;
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		System.out.println(gameState.toString() + " " + player.getName() + " : Received from " + from + ": " + msg);
		gameState.receiveMessage(from, msg);
	}

	public void sendMessage(String from, String to, Message message) throws RemoteException {
		reso.sendMessage(from, to, message);
	}

	public void broadcastMessage(String from, Message message) throws RemoteException {
		reso.broadcastMessage(from, message);
	}

	public void startGame() {
		do {
			gameState.start();
		} while (!(gameState instanceof ExitGameState));

	}

	@Override
	public void run() {
		try {
			startGame();
		} catch (Exception e) {
		}
	}

	public void declarePlayer() {
		try {
			reso.declareClient(player.getName(), this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
