package message;

import game.Player;

public class MsgStepDone extends Message {

	private static final long serialVersionUID = 6423717829327850939L;

	Player player;

	public MsgStepDone(Player player) {
		super();
		this.player = player;
	}

}
