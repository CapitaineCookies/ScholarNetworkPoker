package message;

import JeuCartes.Carte;
import game.gameState.GameState.EGameState;

public class MsgCard extends Message {

	private static final long serialVersionUID = 8100623855212214008L;
	private Carte card;

	public MsgCard(Carte card, EGameState gameState) {
		super(gameState);
		this.card = card;
	}

	public Carte getCard() {
		return card;
	}

}
