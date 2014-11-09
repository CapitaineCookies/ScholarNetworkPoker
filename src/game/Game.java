package game;

import game.gameState.CardsDistributionGameState;
import game.gameState.CardsShowGameState;
import game.gameState.CardsTradeGameState;
import game.gameState.DistribNumberGameState;
import game.gameState.ExitGameState;
import game.gameState.GameState;
import game.gameState.GameState.EGameState;
import game.gameState.PlayerDeclarationGameState;
import game.gameState.PlayerElectionGameState;
import game.gameState.PlayerGetOthersGameState;
import game.gameState.PlayerGetResoGameState;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;

import message.Message;
import reso.Client;
import reso.Reso;

public class Game extends UnicastRemoteObject implements Client, Runnable {

	private static final long serialVersionUID = -4064703456148532918L;

	protected Reso reso;
	protected LocalPlayer player;
	protected Player leader;
	protected GameState currentGameState;
	protected GameState gameStates[];
	protected Set<RemotePlayer> otherPlayers;

	public Game(LocalPlayer player) throws RemoteException {
		super();
		this.reso = null;
		this.otherPlayers = new HashSet<RemotePlayer>();
		this.player = player;
		this.leader = player;

		gameStates = new GameState[EGameState.values().length];

		gameStates[EGameState.getReso.ordinal()] = new PlayerGetResoGameState(this);
		gameStates[EGameState.declaration.ordinal()] = new PlayerDeclarationGameState(this);
		gameStates[EGameState.getOthers.ordinal()] = new PlayerGetOthersGameState(this);
		gameStates[EGameState.distribNumber.ordinal()] = new DistribNumberGameState(this);
		gameStates[EGameState.election.ordinal()] = new PlayerElectionGameState(this);
		gameStates[EGameState.cardsDistribution.ordinal()] = new CardsDistributionGameState(this);
		gameStates[EGameState.cardsTrade.ordinal()] = new CardsTradeGameState(this);
		gameStates[EGameState.cardsShow.ordinal()] = new CardsShowGameState(this);
		gameStates[EGameState.exit.ordinal()] = new ExitGameState(this);

		this.currentGameState = gameStates[EGameState.getReso.ordinal()];
	}

	public Player getNextPlayer() {
		return player.getNextPlayer();
	}

	public LocalPlayer getPlayer() {
		return player;
	}

	public Set<RemotePlayer> getOtherplayers() {
		return otherPlayers;
	}

	public void setReso(Reso reso) {
		this.reso = reso;
	}

	public GameState getCurrentGameState() {
		return this.currentGameState;
	}

	public void setCurrentGameState(EGameState gameState) {
		this.currentGameState = gameStates[gameState.ordinal()];
	}

	public void setOtherPlayer(Set<RemotePlayer> otherPlayers) {
		this.otherPlayers = otherPlayers;
	}

	public Player getLeader() {
		return leader;
	}

	public void setLeader(Player leader) {
		this.leader = leader;
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		System.out.println("[" + player.getName() + "][" + ((Message) msg).getGameState() + "][Recv] from [" + from + "] " + msg);
		gameStates[((Message) msg).getGameState().ordinal()].receiveMessage(from, msg);
		// currentGameState.receiveMessage(from, msg);
	}

	public void sendMessage(String from, String to, Message message) throws RemoteException {
		System.out.println("[" + player.getName() + "][" + message.getGameState() + "][Send]  to  [" + to + "] " + message);
		reso.sendMessage(from, to, message);
	}

	public void sendMessage(String to, Message message) throws RemoteException {
		sendMessage(player.getName(), to, message);
	}

	public void sendMessage(Player to, Message message) throws RemoteException {
		sendMessage(to.getName(), message);
	}

	public void broadcastMessage(String from, Message message) throws RemoteException {
		System.out.println("[" + player.getName() + "][" + message.getGameState() + "][Broadcast] " + message);
		reso.broadcastMessage(from, message);
	}

	public void broadcastMessage(Message message) throws RemoteException {
		reso.broadcastMessage(player.getName(), message);
	}

	public void startGame() {
		do {
			System.out.println("[" + player.getName() + "][" + currentGameState + "][start]");
			currentGameState.start();
		} while (!(currentGameState instanceof ExitGameState));
		System.out.println("[" + player.getName() + "][!" + currentGameState + "!]");
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

	public boolean isLeader() {
		return leader.equals(player);
	}

	public void sendMessageToOther(Message messsage) throws RemoteException {
		for (RemotePlayer otherPlayer : otherPlayers) {
			sendMessage(otherPlayer, messsage);
		}
	}

	public Player getPlayer(String name) {
		for (Player otherPlayer : otherPlayers)
			if (otherPlayer.name.equals(name)) {
				return otherPlayer;
			}

		if (player.name.equals(name)) {
			return player;
		}
		throw new RuntimeException("player : " + name + " unexist !");
	}

	public boolean containPlayer(String name) {
		// TODO test : otherPlayers.contains(name)
		for (RemotePlayer otherPlayer : otherPlayers) {
			if (otherPlayer.name.equals(name)) {
				return true;
			}
		}
		return player.getName().equals(name);
	}

	public GameState getGameState(EGameState eGameState) {
		for (GameState gameState : gameStates)
			if (gameState.getEGameState() == eGameState)
				return gameState;
		throw new RuntimeException("gameState : " + eGameState + " unexist !");
	}

	public void disconnectPlayer() {
		try {
			reso.disconnect(player.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
