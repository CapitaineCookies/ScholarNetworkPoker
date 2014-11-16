package message;

public class MsgPostExeSyncEnd extends Message {

	private static final long serialVersionUID = -9093271132076852522L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
