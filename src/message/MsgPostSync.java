package message;

public class MsgPostSync extends Message {

	private static final long serialVersionUID = 58050461625073585L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
