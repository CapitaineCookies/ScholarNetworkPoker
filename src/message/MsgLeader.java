package message;

/**
 * 
 * @author rgrimmer
 */
public class MsgLeader extends Message {

	private static final long serialVersionUID = 5028715142368247042L;

	private String leaderName;
	private int leaderID;

	public MsgLeader(String leaderName, int leaderID) {
		this.leaderName = leaderName;
		this.leaderID = leaderID;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String p) {
		this.leaderName = p;
	}

	public int getLeaderID() {
		return leaderID;
	}

	public void setLeaderID(int leaderID) {
		this.leaderID = leaderID;
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

	@Override
	public String msgContains() {
		return leaderID + " " + leaderName;
	}

}
