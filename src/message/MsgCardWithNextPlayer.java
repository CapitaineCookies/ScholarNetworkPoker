package message;

import JeuCartes.Carte;
import game.gameState.GameState.EGameState;

public class MsgCardWithNextPlayer extends MsgCard {

	private static final long serialVersionUID = 8100623855212214008L;

	private String nextPlayer;

	public MsgCardWithNextPlayer(Carte card, String nextPlayer, EGameState gameState) {
		super(card, gameState);
		this.nextPlayer = nextPlayer;
	}
	
	public String getNextPlayer() {
		return nextPlayer;
	}

	@Override
	public String msgContains() {
		return super.msgContains() + " " + nextPlayer;
	}

}
