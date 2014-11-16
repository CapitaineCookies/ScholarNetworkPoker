package game.gameState;

import game.LocalPlayer;
import game.OtherPlayers;
import message.MsgPostSynch;
import message.MsgPreSynch;
import reso.Reso;

public abstract class GameStateDecentralized extends GameState {

	public GameStateDecentralized(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers) {
		super(reso, localPlayer, otherPlayers);
	}

	// ///////////////////////
	// Protocol
	// ///////////////////////

	protected void sendPostPreExecuteSynchro() {
		sendToOthers(new MsgPreSynch());
	}

	protected void waitPostPreExecuteSynchro() {
		try {
			log("[WaitPreSynch] expected " + otherPlayers.size() + " message");
			preLock.acquire(otherPlayers.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	protected void sendPrePostExecuteSynchro() {
		sendToOthers(new MsgPostSynch());
	}

	protected void waitPrePostExecuteSynchro() {
		try {
			log("[WaitPostSynch] expected " + otherPlayers.size() + " message");
			postLock.acquire(otherPlayers.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void receive(MsgPreSynch message) {
		preLock.release();
	}

	@Override
	public void receive(MsgPostSynch message) {
		postLock.release();
	}

}
