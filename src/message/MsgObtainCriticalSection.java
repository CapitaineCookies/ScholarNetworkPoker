package message;

import game.gameState.GameState.EGameState;

public class MsgObtainCriticalSection extends Message {

	private static final long serialVersionUID = 7319307127884323767L;

	public MsgObtainCriticalSection(EGameState gameState) {
		super(gameState);
	}

}
