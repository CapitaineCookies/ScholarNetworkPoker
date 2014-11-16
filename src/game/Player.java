package game;

import JeuCartes.Hand;

public abstract class Player {

	String name;
	private Hand hand;

	public Player(String name) {
		super();
		this.hand = new Hand();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player)
			return name.equals(((Player) obj).name);
		else if (obj instanceof String)
			return name.equals((String) obj);
		else
			return false;
	}

	@Override
	public String toString() {
		return name;
	}

	public String toStringHand() {
		return name + " : " + hand;
	}

	public Hand getHand() {
		return hand;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
