package message;

public class MsgIdChoice extends Message {

	private static final long serialVersionUID = -4075650665976010262L;

	int id;

	public MsgIdChoice(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
