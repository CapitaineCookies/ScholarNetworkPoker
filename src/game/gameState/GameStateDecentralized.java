package game.gameState;

import game.LocalPlayer;
import game.OtherPlayers;
import message.MsgPostSync;
import message.MsgPreSync;
import reso.Reso;

public abstract class GameStateDecentralized extends GameState {

	public GameStateDecentralized(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers) {
		super(reso, localPlayer, otherPlayers);
	}

	// ///////////////////////
	// Protocol
	// ///////////////////////

	protected void sendPreExecuteSync() {
		sendToOthers(new MsgPreSync());
	}

	protected void waitPreExecuteSync() {
		try {
			log("[WaitPreSync] expected " + otherPlayers.size() + " message");
			preLock.acquire(otherPlayers.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	protected void sendPostExecuteSync() {
		sendToOthers(new MsgPostSync());
	}

	protected void waitPostExecuteSync() {
		try {
			log("[WaitPostSync] expected " + otherPlayers.size() + " message");
			postLock.acquire(otherPlayers.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void receive(MsgPreSync message) {
		preLock.release();
	}

	@Override
	public void receive(MsgPostSync message) {
		postLock.release();
	}

}
