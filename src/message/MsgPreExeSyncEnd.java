package message;

public class MsgPreExeSyncEnd extends Message {

	private static final long serialVersionUID = 8266711631751638794L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
