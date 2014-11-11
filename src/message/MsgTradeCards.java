package message;

import java.util.List;

import JeuCartes.Carte;

public class MsgTradeCards extends Message {

	private static final long serialVersionUID = -6208920519785895862L;
	List<Carte> cards;

	public MsgTradeCards(List<Carte> cards) {
		this.cards = cards;
	}

	public List<Carte> getCards() {
		return cards;
	}

	@Override
	public String msgContains() {
		return cards.toString();
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}
}
