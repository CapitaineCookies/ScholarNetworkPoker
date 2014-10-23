package message;

public class MsgIdChoice extends Message {

	private static final long serialVersionUID = -4075650665976010262L;

	int id;
	int weight;

	public MsgIdChoice(int id, int weight) {
		this.id = id;
		this.weight = weight;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
