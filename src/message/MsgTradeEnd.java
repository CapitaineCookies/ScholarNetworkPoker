package message;

public class MsgTradeEnd extends Message {

	private static final long serialVersionUID = 6486534621999660141L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
