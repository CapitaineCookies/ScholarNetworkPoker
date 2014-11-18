package game.gameState;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;

import java.util.HashSet;
import java.util.Set;

import message.Message;
import message.MsgPostExeSyncEnd;
import message.MsgPostExecutSyncRing;
import message.MsgPreExeSyncEnd;
import message.MsgPreExecutSyncRing;
import reso.Reso;

public abstract class GameStateRing extends GameState {

	protected final Player leader;

	private enum SyncState {
		notReady, wait, end;
	}

	private SyncState preSyncState;
	private SyncState postSyncState;
	private Set<Integer> bufferPreSync;
	private Set<Integer> bufferPostSync;

	public GameStateRing(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers);
		this.leader = leader;
		this.bufferPreSync = new HashSet<>();
		this.bufferPostSync = new HashSet<>();
		this.preSyncState = SyncState.notReady;
		this.postSyncState = SyncState.notReady;
	}

	@Override
	protected void sendPreExecuteSync() {
		synchronized (bufferPreSync) {
			bufferPreSync.add(localPlayer.getID());
			if (bufferPreSync.size() == otherPlayers.size() + 1) {
				endPreExeSync();
			} else {
				sendToNext(new MsgPreExecutSyncRing(bufferPreSync));
				preSyncState = SyncState.wait;
			}
		}
	}

	@Override
	protected void waitPreExecuteSync() {
		try {
			log("[WaitPreSync] expected " + (otherPlayers.size() + 1) + " ID in message");
			preLock.acquire();
		} catch (InterruptedException e) {
			log("PRE WAIT ERROR !!!!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}
	}

	@Override
	protected void sendPostExecuteSync() {
		synchronized (bufferPostSync) {
			bufferPostSync.add(localPlayer.getID());
			if (bufferPostSync.size() ==  otherPlayers.size() + 1) {
				endPostExeSync();
			}else {
				sendToNext(new MsgPostExecutSyncRing(bufferPostSync));
				postSyncState = SyncState.wait;
			}
		}
	}

	@Override
	protected void waitPostExecuteSync() {
		try {
			log("[WaitPostSync] expected " + (otherPlayers.size() + 1) + " ID in message");
			postLock.acquire();
		} catch (InterruptedException e) {
			log("POST WAIT ERROR !!!!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}
	}

	@Override
	public void receive(MsgPreExeSyncEnd message) {
		synchronized (bufferPreSync) {
			if (preSyncState != SyncState.end) {
				endPreExeSync();
			}
		}
	}

	@Override
	public void receive(MsgPreExecutSyncRing message) {
		synchronized (bufferPreSync) {
			if (preSyncState == SyncState.end)
				return;

			bufferPreSync.addAll(message.getIDs());
			if (bufferPreSync.size() == otherPlayers.size() + 1) {
				endPreExeSync();
			} else if (preSyncState == SyncState.wait)
				sendToNext(new MsgPreExecutSyncRing(bufferPreSync));
		}
	}

	@Override
	public void receive(MsgPostExeSyncEnd message) {
		synchronized (bufferPostSync) {
			if (postSyncState != SyncState.end) {
				endPostExeSync();
			}
		}
	}

	@Override
	public void receive(MsgPostExecutSyncRing message) {
		synchronized (bufferPostSync) {
			if (postSyncState == SyncState.end)
				return;

			bufferPostSync.addAll(message.getIDs());
			if (bufferPostSync.size() == otherPlayers.size() + 1) {
				endPostExeSync();
			} else if (postSyncState == SyncState.wait) {
				sendToNext(new MsgPostExecutSyncRing(bufferPostSync));
			}
		}
	}

	private void endPreExeSync() {
		sendToNext(new MsgPreExeSyncEnd());
		log("[SetPreSyncStateEnd]");
		preSyncState = SyncState.end;
		log("[ReleasePreLock]");
		preLock.release();
	}

	private void endPostExeSync() {
		sendToNext(new MsgPostExeSyncEnd());
		log("[SetPostSyncStateEnd]");
		postSyncState = SyncState.end;
		log("[ReleasePostLock]");
		postLock.release();
	}

	public void sendToNext(Message message) {
		super.send(localPlayer.getNextPlayer(), message);
	}

	public void sendToLeader(Message message) {
		super.send(leader, message);
	}

	@Override
	@Deprecated
	public void send(Player to, Message msg) {
		super.send(to, msg);
	}

	@Override
	@Deprecated
	public void send(String to, Message message) {
		super.send(to, message);
	}

	// Players

	public boolean isLeader() {
		return leader.equals(localPlayer);
	}

}
