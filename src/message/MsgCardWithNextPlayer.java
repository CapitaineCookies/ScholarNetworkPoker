package message;

import JeuCartes.Carte;

public class MsgCardWithNextPlayer extends MsgCard {

	private static final long serialVersionUID = 8100623855212214008L;

	private String nextPlayer;

	public MsgCardWithNextPlayer(Carte card, String nextPlayer) {
		super(card);
		this.nextPlayer = nextPlayer;
	}

	public String getNextPlayer() {
		return nextPlayer;
	}

	@Override
	public String msgContains() {
		return super.msgContains() + " " + nextPlayer;
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
