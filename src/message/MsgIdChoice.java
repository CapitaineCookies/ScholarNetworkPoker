package message;

import game.gameState.GameState.EGameState;

public class MsgIdChoice extends Message {

	private static final long serialVersionUID = -4075650665976010262L;

	int id;

	public MsgIdChoice(int id, EGameState gameState) {
		super(gameState);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String msgContains() {
		return Integer.toString(id);
	}
}
