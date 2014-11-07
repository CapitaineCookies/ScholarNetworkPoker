package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import message.MsgPlayers;
import message.MsgPlaying;
import message.MsgPlayingToo;
import message.MsgStepDone;

public class GetPlayersGameState extends GameState {

	private List<Player> playersWantToPlay;
	private ScheduledThreadPoolExecutor scheduler;
	private boolean recieveplayers;

	public GetPlayersGameState(Game game) {
		super(game);
		this.recieveplayers = false;
		this.playersWantToPlay = game.getOtherplayer();
		this.scheduler = new ScheduledThreadPoolExecutor(1);
	}

	@Override
	public synchronized void receiveMessage(String from, Serializable msg) throws RemoteException {

		// TODO synchronized
		// TODO check list send, contain player twice !
		if (msg instanceof MsgPlayers && !recieveplayers) {
			recieveplayers = true;
			MsgPlayers msgPlayers = (MsgPlayers) msg;
			playersWantToPlay = msgPlayers.getPlayers();
			scheduler.shutdown();
			notifyStepDone();
		} else if (msg instanceof MsgPlaying) {
			playersWantToPlay.add(new Player(from));
			if (!getPlayerName().equals(from)) {
				game.sendMessage(getPlayerName(), from, new MsgPlayingToo(EGameState.getPlayers));
			}

		} else if (msg instanceof MsgPlayingToo) {
			if (!playersWantToPlay.contains(from)) {
				playersWantToPlay.add(new Player(from));
			}

		} else if (msg instanceof MsgStepDone) {
			super.receiveStepDone(from);

		} else {
			ignoredMessage(from, msg);
		}
	}

	@Override
	public void start() {
		startChrono();
		sendPlayingMessage();
		waitStepDone();
		goToNextStep();
	}

	private void startChrono() {
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					game.broadcastMessage(game.getPlayer().getName(), new MsgPlayers(playersWantToPlay,
							EGameState.getPlayers));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, 20, TimeUnit.SECONDS);
	}

	private void sendPlayingMessage() {
		String playerName = game.getPlayer().getName();
		try {
			game.broadcastMessage(playerName, new MsgPlaying(EGameState.getPlayers));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void goToNextStep() {
		// Check if my name is include in the list of player
		if (playersWantToPlay.contains(game.getPlayer())) {
			// Set other player list without me
			playersWantToPlay.remove(game.getPlayer());
			game.setOtherPlayer(playersWantToPlay);

			sendMsgStepDoneToOther(EGameState.getPlayers);
			waitOtherPlayersDone();

			game.setCurrentGameState(EGameState.distribNumber);

		} else {
			// Player arrived too late
			game.setCurrentGameState(EGameState.exit);
		}
	}

	@Override
	public EGameState getEGameState() {
		return EGameState.getPlayers;
	}
}
