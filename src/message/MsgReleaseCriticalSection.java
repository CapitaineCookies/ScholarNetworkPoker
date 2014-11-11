package message;

public class MsgReleaseCriticalSection extends Message {

	private static final long serialVersionUID = -2972886970648689939L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}
}
