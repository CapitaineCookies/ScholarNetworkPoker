package message;

public class MsgReceiveToken extends Message {
	
	private static final long serialVersionUID = -4402373701828939523L;
	int senderID;
	
	public MsgReceiveToken(int senderID) {
		this.senderID = senderID;
	}
	
	public int getSenderID() {
		return senderID;
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

	@Override
	public String msgContains() {
		return "ID : " + senderID;
	}

}
