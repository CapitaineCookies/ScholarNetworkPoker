package message;

import game.gameState.GameState.EGameState;

import java.util.List;

import JeuCartes.Carte;

public class MsgTradeCards extends Message {

	private static final long serialVersionUID = -6208920519785895862L;
	List<Carte> cards;

	public MsgTradeCards(List<Carte> cards, EGameState gameState) {
		super(gameState);
		this.cards = cards;
	}

	public List<Carte> getCards() {
		return cards;
	}

}
