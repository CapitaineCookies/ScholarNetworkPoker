package game;

public class LocalPlayer extends Player {
	
	int id;

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
}
