package message;

public class MsgPreSynch extends Message {

	private static final long serialVersionUID = 7445833237636854901L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
