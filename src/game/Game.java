package game;

import game.gameState.CardsDistributionGameState;
import game.gameState.CardsTradeGameState;
import game.gameState.DeclarePlayerGameState;
import game.gameState.DistribNumberGameState;
import game.gameState.ElectionGameState;
import game.gameState.ExitGameState;
import game.gameState.GameState;
import game.gameState.GameState.EGameState;
import game.gameState.GetPlayersGameState;
import game.gameState.GetResoGameState;
import game.gameState.CardsShowGameState;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import message.Message;
import message.MsgCard;
import reso.Client;
import reso.Reso;

public class Game extends UnicastRemoteObject implements Client, Runnable {

    private static final long serialVersionUID = -4064703456148532918L;

    protected Reso reso;
    protected Player player;
    protected Player leader;
    protected GameState currentGameState;
    protected GameState gameStates[];
    protected List<Player> otherPlayers;

    public Game(Player player) throws RemoteException {
        super();
        this.reso = null;
        this.otherPlayers = null;
        this.player = player;
        this.leader = player;

        gameStates = new GameState[EGameState.values().length];

        gameStates[EGameState.declarePlayer.ordinal()] = new DeclarePlayerGameState(this);
        gameStates[EGameState.distribNumber.ordinal()] = new DistribNumberGameState(this);
        gameStates[EGameState.exit.ordinal()] = new ExitGameState(this);
        gameStates[EGameState.getPlayers.ordinal()] = new GetPlayersGameState(this);
        gameStates[EGameState.getReso.ordinal()] = new GetResoGameState(this);
        gameStates[EGameState.election.ordinal()] = new ElectionGameState(this); 
        gameStates[EGameState.cardsDistribution.ordinal()] = new CardsDistributionGameState(this);
        gameStates[EGameState.cardsTrade.ordinal()] = new CardsTradeGameState(this);
        gameStates[EGameState.cardsShow.ordinal()] = new CardsShowGameState(this);

        this.currentGameState = gameStates[EGameState.getReso.ordinal()];
    }

    public Player getNextPlayer() {
        Player maxPlayer = getMax(otherPlayers);
        if (maxPlayer == player) {
            return getMin(otherPlayers);
        }

        Player nextPlayer = maxPlayer;
        for (Player p : otherPlayers) {
            if (p.getID() < nextPlayer.getID() && p.getID() > player.getID()) {
                nextPlayer = p;
            }
        }

        return nextPlayer;
    }

    private Player getMin(List<Player> otherPlayers) {
        Player minPlayer = getPlayer();
        for (Player p : otherPlayers) {
            if (p.getID() < minPlayer.getID()) {
                minPlayer = p;
            }
        }
        return minPlayer;
    }

    private Player getMax(List<Player> otherPlayers) {
        Player minPlayer = getPlayer();
        for (Player p : otherPlayers) {
            if (p.getID() > minPlayer.getID()) {
                minPlayer = p;
            }
        }
        return minPlayer;
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

    public GameState getCurrentGameState() {
        return this.currentGameState;
    }

    public void setCurrentGameState(EGameState gameState) {
        this.currentGameState = gameStates[gameState.ordinal()];
    }

    public void setOtherPlayer(List<Player> otherPlayers) {
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
        System.out.println(((Message) msg).getGameState() + " " + player.getName() + " : Received from " + from + ": " + msg);
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
        for (Player otherPlayer : otherPlayers) {
            if (otherPlayer.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

	public boolean isLeader() {
		return leader == player;
	}

	public void sendMessageToOther(MsgCard msgCard) throws RemoteException {
		for (Player otherPlayer : otherPlayers) {
			sendMessage(otherPlayer, msgCard);
		}
	}

	public Player getPlayer(String name) {
		for (Player otherPlayer : otherPlayers)
			if (otherPlayer.name.equals(name))
                return otherPlayer;
            
		if(player.name.equals(name))
			return player;
		throw new RuntimeException("player : " + name + " unexist !");
	}
}
