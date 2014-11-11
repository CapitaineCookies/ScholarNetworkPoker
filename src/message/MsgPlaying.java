package message;

public class MsgPlaying extends Message {

	private static final long serialVersionUID = -3878682244225160047L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}
}
