package game;

import game.gameState.DeclarePlayerGameState;
import game.gameState.DistribNumberGameState;
import game.gameState.ExitGameState;
import game.gameState.GameState;
import game.gameState.GameState.EGameState;
import game.gameState.GetPlayersGameState;
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
	protected GameState currentGameState;
	protected GameState gameStates[];
	protected List<Player> otherPlayers;

	public Game(Player player) throws RemoteException {
		super();
		this.reso = null;
		this.otherPlayers = null;
		this.player = player;

		gameStates = new GameState[EGameState.values().length];

		gameStates[EGameState.declarePlayer.ordinal()] = new DeclarePlayerGameState(this);
		gameStates[EGameState.distribNumber.ordinal()] = new DistribNumberGameState(this);
		gameStates[EGameState.exit.ordinal()] = new ExitGameState(this);
		gameStates[EGameState.getPlayers.ordinal()] = new GetPlayersGameState(this);
		gameStates[EGameState.getReso.ordinal()] = new GetResoGameState(this);
		gameStates[EGameState.cardsDistribution.ordinal()] = new GetResoGameState(this);

		this.currentGameState = gameStates[EGameState.getReso.ordinal()];
	}

	public Player getPlayer() {
		return player;
	}

	public List<Player> getOtherplayer() {
		return otherPlayers;
	}

	public void setReso(Reso reso) {
		this.reso = reso;
	}

	public void setCurrentGameState(EGameState gameState) {
		this.currentGameState = gameStates[gameState.ordinal()];
	}

	public void setOtherPlayer(List<Player> otherPlayers) {
		this.otherPlayers = otherPlayers;
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		System.out.println(currentGameState.toString() + " " + player.getName() + " : Received from " + from + ": " + msg);
		gameStates[((Message) msg).getGameState().ordinal()].receiveMessage(from, msg);
		// currentGameState.receiveMessage(from, msg);
	}

	public void sendMessage(String from, String to, Message message) throws RemoteException {
		reso.sendMessage(from, to, message);
	}

	public void sendMessage(String to, Message message) throws RemoteException {
		reso.sendMessage(player.getName(), to, message);
	}

	public void sendMessage(Player to, Message message) throws RemoteException {
		sendMessage(to.getName(), message);
	}

	public void broadcastMessage(String from, Message message) throws RemoteException {
		reso.broadcastMessage(from, message);
	}

	public void broadcastMessage(Message message) throws RemoteException {
		reso.broadcastMessage(player.getName(), message);
	}

	public void startGame() {
		do {
			System.out.println(player.getName() + " " + currentGameState + " start");
			currentGameState.start();
		} while (!(currentGameState instanceof ExitGameState));

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

	public boolean containPlayer(String name) {
		for (Player otherPlayer : otherPlayers)
			if (otherPlayer.name.equals(name))
				return true;
		return false;
	}
}
