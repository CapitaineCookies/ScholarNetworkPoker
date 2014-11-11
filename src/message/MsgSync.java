package message;

/**
 * 
 * @author rgrimmer
 */
public class MsgSync extends Message {

	private static final long serialVersionUID = -5573026295437888232L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}
}
