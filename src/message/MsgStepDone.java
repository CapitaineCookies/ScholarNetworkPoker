package message;

import game.gameState.GameState.EGameState;

public class MsgStepDone extends Message {

	private static final long serialVersionUID = 6423717829327850939L;

	public MsgStepDone(EGameState gameState) {
		super(gameState);
	}

	@Override
	public String msgContains() {
		return "";
	}

}
