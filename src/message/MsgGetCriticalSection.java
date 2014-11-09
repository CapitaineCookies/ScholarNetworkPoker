package message;

import game.gameState.GameState.EGameState;

public class MsgGetCriticalSection extends Message {

	private static final long serialVersionUID = -1055960342002531870L;

	public MsgGetCriticalSection(EGameState gameState) {
		super(gameState);
	}

	@Override
	public String msgContains() {
		return "";
	}

}
