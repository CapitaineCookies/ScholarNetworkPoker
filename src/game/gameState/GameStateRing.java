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
	protected void sendPostPreExecuteSynchro() {
		synchronized (bufferPreSync) {
			bufferPreSync.add(localPlayer.getID());
			if (bufferPreSync.size() < otherPlayers.size() + 1) {
				sendToNext(new MsgPreExecutSyncRing(bufferPreSync));
				preSyncState = SyncState.wait;
			} else {
				sendToNext(new MsgPreExeSyncEnd());
				preSyncState = SyncState.end;
			}
		}
	}

	@Override
	protected void waitPostPreExecuteSynchro() {
		try {
			log("[WaitPreSynch] expected " + (otherPlayers.size() + 1) + " ID in message");
			preLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void sendPrePostExecuteSynchro() {
		synchronized (bufferPostSync) {
			bufferPostSync.add(localPlayer.getID());
			if (bufferPostSync.size() < otherPlayers.size() + 1) {
				sendToNext(new MsgPostExecutSyncRing(bufferPostSync));
				postSyncState = SyncState.wait;
			} else {
				sendToNext(new MsgPostExeSyncEnd());
				postSyncState = SyncState.end;
			}
		}
	}

	@Override
	protected void waitPrePostExecuteSynchro() {
		try {
			log("[WaitPostSynch] expected " + otherPlayers.size() + 1 + " ID in message");
			postLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void receive(MsgPreExeSyncEnd message) {
		synchronized (bufferPreSync) {
			if (preSyncState != SyncState.end) {
				sendToNext(message);
				preLock.release();
			}
			preSyncState = SyncState.end;
		}
	}

	@Override
	public void receive(MsgPreExecutSyncRing message) {
		synchronized (bufferPreSync) {
			if (preSyncState == SyncState.end)
				return;

			bufferPreSync.addAll(message.getIDs());
			if (bufferPreSync.size() == otherPlayers.size() + 1) {
				sendToNext(new MsgPreExeSyncEnd());
				preSyncState = SyncState.end;
				preLock.release();
			} else if (preSyncState == SyncState.wait)
				sendToNext(new MsgPreExecutSyncRing(bufferPreSync));
		}
	}

	@Override
	public void receive(MsgPostExeSyncEnd message) {
		synchronized (bufferPostSync) {
			if (postSyncState != SyncState.end) {
				sendToNext(message);
				postLock.release();
			}
			postSyncState = SyncState.end;
		}
	}

	@Override
	public void receive(MsgPostExecutSyncRing message) {
		synchronized (bufferPostSync) {
			if (postSyncState == SyncState.end)
				return;

			bufferPostSync.addAll(message.getIDs());
			if (bufferPostSync.size() == otherPlayers.size() + 1) {
				sendToNext(new MsgPostExeSyncEnd());
				postSyncState = SyncState.end;
				postLock.release();
			} else if (postSyncState == SyncState.wait)
				sendToNext(new MsgPostExecutSyncRing(bufferPostSync));
		}
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
