package message;

public class MsgObtainCriticalSection extends Message {

	private static final long serialVersionUID = 7319307127884323767L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
