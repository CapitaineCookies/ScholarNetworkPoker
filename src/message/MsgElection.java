package message;

/**
 * 
 * @author rgrimmer
 */
public class MsgElection extends Message {
	private static final long serialVersionUID = 596680779647428670L;
	private int id;

	public MsgElection(int id) {
		this.id = id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	@Override
	public String msgContains() {
		return Integer.toString(id);
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}
}
