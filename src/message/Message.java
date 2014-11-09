package message;

import game.gameState.GameState.EGameState;

import java.io.Serializable;

public abstract class Message implements Serializable {

	private static final long serialVersionUID = 880439679422242011L;

	EGameState gameState;

	public Message(EGameState gameState) {
		this.gameState = gameState;
	}

	private String msgName() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		String msgContains = msgContains();
		if (msgContains.isEmpty())
			return "[" + msgName() + "]";
		else
			return "[" + msgName() + " : " + msgContains + "]";
	}

	public abstract String msgContains();

	public EGameState getGameState() {
		return gameState;
	}

}
