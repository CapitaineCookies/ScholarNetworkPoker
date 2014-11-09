package game;

public class LocalPlayer extends Player {
	
	private int id;
	private Player nextPlayer;
	private Player previousPlayer;

	public LocalPlayer(String name) {
		super(name);
		this.id = 0;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return this.id;
	}

	public Player getNextPlayer() {
		return nextPlayer;
	}

	public void setNextPlayer(Player nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	public void setPreviousPlayer(Player previouslayer) {
		this.previousPlayer = previouslayer;
	}

	public Player getPreviousPlayer() {
		return previousPlayer;
	}
}
