package message;

/**
 * 
 * @author rgrimmer
 */
public class MsgPlayingToo extends Message {

	private static final long serialVersionUID = -8881890658503260381L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}
}
