package message;

import JeuCartes.Carte;

public class MsgCard extends Message {

	private static final long serialVersionUID = 8100623855212214008L;

	private Carte card;

	public MsgCard(Carte card) {
		this.card = card;
	}

	public Carte getCard() {
		return card;
	}

	@Override
	public String msgContains() {
		return card.toString();
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
