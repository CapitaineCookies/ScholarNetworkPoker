package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import message.MsgPlaying;
import message.MsgPlayers;

public class GetPlayersGameState extends GameState {

	private List<Player> playersWantToPlay;
	private ScheduledThreadPoolExecutor scheduler;
	private boolean reiciveplayers;

	public GetPlayersGameState(Game game) {
		super(game);
		this.reiciveplayers = false;
		this.playersWantToPlay = new Vector<Player>();
		this.playersWantToPlay.add(game.getPlayer());
		this.scheduler = new ScheduledThreadPoolExecutor(1);
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {

		// TODO synchronized
		if (msg instanceof MsgPlayers) {
			MsgPlayers msgPlayers = (MsgPlayers) msg;
			playersWantToPlay = msgPlayers.getPlayers();
			scheduler.shutdown();
			notifyStepDone();
		} else if (msg instanceof MsgPlaying) {
			MsgPlaying msgIPlay = (MsgPlaying) msg;
			playersWantToPlay.add(new Player(msgIPlay.getPlayerName()));
		} else {
			ignoredMessage(from, msg);
		}
	}

	@Override
	public void start() {
		sendPlayingMessage();
		startChrono();
		waitStepDone();
		goToNextStep();
	}

	private void startChrono() {
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					game.broadcastMessage(game.getPlayer().getName(), new MsgPlayers(playersWantToPlay));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, 10, TimeUnit.SECONDS);
	}

	private void sendPlayingMessage() {
		String playerName = game.getPlayer().getName();
		try {
			game.broadcastMessage(playerName, new MsgPlaying(playerName));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void goToNextStep() {
		// Check if my name is include in the list of player
                String players = game.getPlayer().getName() + " -> [";
                for (Player p : playersWantToPlay) {
                    players += p.getName() + " ";
                }
                players += "]";
                System.out.println(players);
		if (playersWantToPlay.contains(game.getPlayer())) {
			// Set other player list without me
			playersWantToPlay.remove(game.getPlayer());
			game.setOtherPlayer(playersWantToPlay);
			game.setGameState(new DistribNumberGameState(game));
		} else {
			// Player arrived too late
			game.setGameState(new ExitGameState(game));
		}
	}

}
