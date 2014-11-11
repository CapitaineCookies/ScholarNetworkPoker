package obsolete;

import game.gameState.GameState.EGameState;

public class MessageHeader {

	private String from;
	private EGameState gameState;

	public MessageHeader(String from, EGameState gameState) {
		super();
		this.from = from;
		this.gameState = gameState;
	}

	public String getFrom() {
		return from;
	}

	public EGameState getGameState() {
		return gameState;
	}

}
