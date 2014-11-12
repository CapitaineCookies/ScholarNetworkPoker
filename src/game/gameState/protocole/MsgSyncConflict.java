package game.gameState.protocole;

import message.Message;
import message.MessageVisitor;

public class MsgSyncConflict extends Message {

	private static final long serialVersionUID = 1279966659089569258L;

	@Override
	public String msgContains() {
		return "";
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
