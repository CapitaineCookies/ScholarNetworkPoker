package game;

import game.gameState.GameState;
import game.gameState.GameState.EGameState;
import game.gameState.protocole.A_GetResoGameState;
import game.gameState.protocole.B_DeclarationGameState;
import game.gameState.protocole.C_GetOthersGameState;
import game.gameState.protocole.D_NumberDistribGameState;
import game.gameState.protocole.E_ElectionGameState;
import game.gameState.protocole.FL_CardsDistribGameState;
import game.gameState.protocole.F_CardsDistribGameState;
import game.gameState.protocole.GL_CardsTradeGameState;
import game.gameState.protocole.G_CardsTradeGameState;
import game.gameState.protocole.H_CardsShowGameState;
import game.gameState.protocole.Z_ExitGameState;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import message.Message;
import reso.Client;
import reso.Reso;

public class Game extends UnicastRemoteObject implements Client {

	private static final long serialVersionUID = -4064703456148532918L;

	protected Reso reso;
	protected LocalPlayer localPlayer;
	protected Player leader;
	protected GameState currentGameState;
	protected OtherPlayers otherPlayers;
	protected Map<EGameState, GameState> gameStates;

	public Game(LocalPlayer localPlayer) throws RemoteException {
		super();
		this.reso = null;
		this.otherPlayers = new OtherPlayers();
		this.localPlayer = localPlayer;
		this.leader = localPlayer;
		this.gameStates = new HashMap<>();
	}

	public void setReso(Reso reso) {
		this.reso = reso;
	}

	public GameState getCurrentGameState() {
		return this.currentGameState;
	}

	private void setNextGameState() {
		this.currentGameState = getGameState(currentGameState.getNextState());
	}

	public void addAll(Set<RemotePlayer> otherPlayers) {
		this.otherPlayers.addAll(otherPlayers);
		;
	}

	public Player getLeader() {
		return leader;
	}

	public void setLeader(Player leader) {
		this.leader = leader;
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		if (msg instanceof Message) {
			Message message = (Message) msg;
			GameState gameState = getGameState(((Message) msg).getSenderGameState());
			gameState.log("[Recv] from [" + from + "] " + msg);
			message.setFrom(from);
			message.accept(gameState);
		} else {
			throw new RemoteException("ERROR : Receive non message type !");
		}
	}

	public void startGame() {
		currentGameState = getGameState(EGameState.A_getReso);
		do {
			currentGameState.start();
			setNextGameState();
		} while (!(currentGameState.getGameState() == EGameState.Z_exit));
		currentGameState.start();
	}

	public GameState getGameState(EGameState eGameState) {
		if (!gameStates.containsKey(eGameState))
			createGameState(eGameState);
		return gameStates.get(eGameState);
	}

	private void createGameState(EGameState eGameState) {
		GameState gameState = null;
		switch (eGameState) {
		case A_getReso:
			gameState = new A_GetResoGameState(localPlayer, this);
			break;
		case B_declaration:
			gameState = new B_DeclarationGameState(reso, localPlayer, this);
			break;
		case C_getOthers:
			gameState = new C_GetOthersGameState(reso, localPlayer, otherPlayers);
			break;
		case D_distribNumber:
			gameState = new D_NumberDistribGameState(reso, localPlayer, otherPlayers);
			break;
		case E_election:
			gameState = new E_ElectionGameState(reso, localPlayer, otherPlayers, this);
			break;
		case F_cardsDistribution:
			if (!isLeader()) {
				gameState = new F_CardsDistribGameState(reso, localPlayer, otherPlayers, leader);
			} else {
				gameState = new FL_CardsDistribGameState(reso, localPlayer, otherPlayers, leader);
			}
			break;
		case G_cardsTrade:
			if (!isLeader()) {
				gameState = new G_CardsTradeGameState(reso, localPlayer, otherPlayers, leader);
			} else {
				FL_CardsDistribGameState cardsDistrib = (FL_CardsDistribGameState) getGameState(EGameState.F_cardsDistribution);
				gameState = new GL_CardsTradeGameState(reso, localPlayer, otherPlayers, leader, cardsDistrib.getDeck());
			}
			break;
		case H_cardsShow:
			gameState = new H_CardsShowGameState(reso, localPlayer, otherPlayers, leader);
			break;
		case Z_exit:
			gameState = new Z_ExitGameState(reso, localPlayer);
			break;
		default:
			throw new RuntimeException("Can't create gameState from " + eGameState);
		}
		gameStates.put(eGameState, gameState);
	}

	private boolean isLeader() {
		return localPlayer.equals(leader);
	}

	@Override
	public String toString() {
		return localPlayer.toString();
	}
}
