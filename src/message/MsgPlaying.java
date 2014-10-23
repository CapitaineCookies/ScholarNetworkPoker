package message;

public class MsgPlaying extends Message {

	private static final long serialVersionUID = -3878682244225160047L;
	String playerName;

	public MsgPlaying(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}
}
