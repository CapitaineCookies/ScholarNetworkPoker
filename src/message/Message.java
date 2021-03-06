package message;

import game.gameState.GameState.EGameState;

import java.io.Serializable;

public abstract class Message implements Serializable {

	private static final long serialVersionUID = 880439679422242011L;

	String from = null;
	EGameState senderGameState = null;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public EGameState getSenderGameState() {
		return senderGameState;
	}

	public void setSenderGameState(EGameState senderGameState) {
		this.senderGameState = senderGameState;
	}

	public abstract void accept(MessageVisitor visitor);

	public abstract String msgContains();

	private String msgName() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		String msgContains = msgContains();
		if (msgContains.isEmpty())
			return "[" + msgName() + "]";
		else
			return "[" + msgName() + " | " + msgContains + "]";
	}

}
