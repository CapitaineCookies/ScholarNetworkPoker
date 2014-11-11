package message;

public class MsgGetCriticalSection extends Message {

	private static final long serialVersionUID = -1055960342002531870L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
