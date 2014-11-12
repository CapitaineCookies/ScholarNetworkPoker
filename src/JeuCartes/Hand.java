package JeuCartes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Hand implements Serializable {
	
    public static final int nbCardPerPlayer = 5;

	
	List<Carte> handCards;

	public Hand() {
		handCards = new LinkedList<Carte>();
	}
	
	public void add(Carte card) {
		handCards.add(card);
	}
	
	public void remove(Carte card) {
		if(!handCards.remove(card))
			throw new RuntimeException();
	}

	public List<Carte> getRandomCards(int nbCardTrad) {
		List<Carte> cards = new ArrayList<>(nbCardTrad);
		
		for(int i = 0; i < nbCardTrad; ++i)
			cards.add(pollRandomCard());
		
		return cards;
	}

	public Carte pollRandomCard() {
		Collections.shuffle(handCards);
		return handCards.remove(handCards.size()-1);
	}

	public boolean isEmpty() {
		return handCards.isEmpty();
	}
        
        public int getSize() {
            return handCards.size();
        }

    @Override
    public String toString() {
        return handCards.toString();
    }
}
