package message;

import java.util.Set;

public class MsgPreExecutSyncRing extends MsgSyncRing {

	private static final long serialVersionUID = 5992298661173230826L;

	public MsgPreExecutSyncRing(Set<Integer> ids) {
		super(ids);
	}
	
	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
