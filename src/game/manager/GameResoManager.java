package game.manager;

import game.LocalPlayer;
import game.Player;
import game.RemotePlayer;
import game.gameState.GameState.EGameState;

import java.rmi.RemoteException;
import java.util.Map;

import message.Message;
import reso.Reso;

public class GameResoManager {
	
	private Reso reso;
	LocalPlayer localPlayer;
	Map<String, RemotePlayer> otherPlayers;	
	EGameState currentGameState;

	public GameResoManager(Reso reso, LocalPlayer localPlayer, Map<String, RemotePlayer> otherPlayers, EGameState currentGameState) {
		this.reso = reso;
		this.currentGameState = currentGameState;
	}

	private void send(String to, Message msg) {
		msg.setSenderGameState(currentGameState);
		try {
			reso.sendMessage(localPlayer.getName(), to, msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void send(Player to, Message msg) {
		send(to.getName(), msg);
	}

	public void sendToOthers(Message msg) {
		for (Player player : otherPlayers.values())
			send(player, msg);
	}

	public void broadcastMessage(String from, Message message) throws RemoteException {
		System.out.println("[" + localPlayer.getName() + "][" + message.getSenderGameState() + "][Broadcast] " + message);
		reso.broadcastMessage(from, message);
	}

	public void broadcastMessage(Message message) throws RemoteException {
		reso.broadcastMessage(localPlayer.getName(), message);
	}



	public void setReso(Reso reso) {
		this.reso = reso;
	}
	
}
