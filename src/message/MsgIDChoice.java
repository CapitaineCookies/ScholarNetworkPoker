package message;

public class MsgIDChoice extends Message {

	private static final long serialVersionUID = -4075650665976010262L;

	int id;

	public MsgIDChoice(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String msgContains() {
		return Integer.toString(id);
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}
}
