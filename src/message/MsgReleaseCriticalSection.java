package message;

import game.gameState.GameState.EGameState;

public class MsgReleaseCriticalSection extends Message {

	private static final long serialVersionUID = -2972886970648689939L;

	public MsgReleaseCriticalSection(EGameState gameState) {
		super(gameState);
	}

}
