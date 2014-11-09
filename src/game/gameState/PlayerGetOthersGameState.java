package game.gameState;

import game.Game;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import message.MsgPlayers;
import message.MsgPlaying;
import message.MsgPlayingToo;
import message.MsgStepDone;

public class PlayerGetOthersGameState extends GameState {

	private Set<String> playersWantToPlay;
	private ScheduledThreadPoolExecutor scheduler;
	private boolean recieveplayers;

	public PlayerGetOthersGameState(Game game) {
		super(game);

		this.recieveplayers = false;
		this.playersWantToPlay = Collections.synchronizedSet(new HashSet<String>());
		this.scheduler = new ScheduledThreadPoolExecutor(1);
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {

		if (msg instanceof MsgPlayers) {
			receiveMsgPlayers(from, (MsgPlayers) msg);
		} else if (msg instanceof MsgPlaying) {
			if (playersWantToPlay.add(from)) {
				if (!getPlayerName().equals(from)) {
					game.sendMessage(getPlayerName(), from, new MsgPlayingToo(EGameState.getOthers));
				}
			}
		} else if (msg instanceof MsgPlayingToo) {
			playersWantToPlay.add(from);
		} else if (msg instanceof MsgStepDone) {
			super.receiveStepDone(from);

		} else {
			ignoredMessage(from, msg);
		}
	}

	// Lock for recieveplayers boolean
	private Lock receiveLocker = new ReentrantLock();
	private void receiveMsgPlayers(String from, MsgPlayers msg) {
		receiveLocker.lock();
		if (!recieveplayers) {
			recieveplayers = true;
			receiveLocker.unlock();

			game.setOtherPlayer(msg.getPlayers());
			System.out.println("OthersPlayers Set !!");
			scheduler.shutdown();
			notifyStepDone();

		} else {
			receiveLocker.unlock();
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
					game.broadcastMessage(getPlayerName(), new MsgPlayers(playersWantToPlay, getEGameState()));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, 12, TimeUnit.SECONDS);
	}

	private void sendPlayingMessage() {
		String playerName = game.getPlayer().getName();
		try {
			game.broadcastMessage(playerName, new MsgPlaying(EGameState.getOthers));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void goToNextStep() {

		// Remove him from container if false, he don't play
		if (game.getOtherplayers().remove(game.getPlayer())) {

			sendMsgStepDoneToOther(EGameState.getOthers);
			game.setCurrentGameState(EGameState.distribNumber);
			waitOtherPlayersDone();


		} else {
			// Player arrived too late
			game.setCurrentGameState(EGameState.exit);
		}
	}

	@Override
	public EGameState getEGameState() {
		return EGameState.getOthers;
	}
}
