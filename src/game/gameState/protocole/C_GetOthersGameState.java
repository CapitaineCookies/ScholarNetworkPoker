package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.gameState.GameStateDecentralized;

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
import reso.Reso;

public class C_GetOthersGameState extends GameStateDecentralized {

	private Set<String> playersWantToPlay;
	private ScheduledThreadPoolExecutor scheduler;
	private boolean recieveplayers;

	public C_GetOthersGameState(Reso reso, LocalPlayer player, OtherPlayers others) {
		super(reso, player, others);
		this.recieveplayers = false;
		this.playersWantToPlay = Collections.synchronizedSet(new HashSet<String>());
		this.scheduler = new ScheduledThreadPoolExecutor(1);
		this.scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
	}

	@Override
	protected void execute() {
		startChrono();
		sendPlayingMessage();
		// If used post sync, must put removePlayer HERE !
	}

	// Lock for recieveplayers boolean
	private Lock receiveLocker = new ReentrantLock();

	@Override
	public void receive(MsgPlayers msg) {

		receiveLocker.lock();
		if (!recieveplayers) {
			recieveplayers = true;
			receiveLocker.unlock();

			setOtherPlayer(msg.getPlayers());
			log("[Set] OthersPlayers");
			scheduler.shutdown();
			notifyStepDone();

		} else {
			receiveLocker.unlock();
			ignoreMessage(msg);
		}
	}

	@Override
	public void receive(MsgPlaying msg) {
		if (playersWantToPlay.add(msg.getFrom())) {
			if (!comeFromLocalPlayer(msg)) {
				send(msg.getFrom(), new MsgPlayingToo());
			}
		}
	}

	@Override
	public void receive(MsgPlayingToo msg) {
		playersWantToPlay.add(msg.getFrom());
	}

	private boolean removeLocalPlayer() {
		return otherPlayers.remove(localPlayer);
	}

	private void startChrono() {
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				broadcast(new MsgPlayers(playersWantToPlay));
			}
		}, 18, TimeUnit.SECONDS);
	}

	private void sendPlayingMessage() {
		broadcast(new MsgPlaying());
	}

	@Override
	public EGameState getGameState() {
		return EGameState.C_getOthers;
	}

	@Override
	public EGameState getNextState() {
		return (removeLocalPlayer()) ? EGameState.D_distribNumber : EGameState.Z_exit;
	}
}
