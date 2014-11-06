package game;

import java.io.Serializable;

import JeuCartes.Hand;

public class Player implements Serializable {


    private static final long serialVersionUID = -2777283647638357088L;

    int id;
    String name;

	private Hand hand;
    
    public Player(String name) {
        super();
        this.hand = new Hand();
        this.name = name;
        this.id = 0;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player) obj;
        return name.equals(other.name);
    }

    @Override
    public String toString() {
        return name + " " + id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

	public Hand getHand() {
		return hand;
	}
}
