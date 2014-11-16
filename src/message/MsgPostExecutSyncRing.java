package message;

import java.util.Set;

public class MsgPostExecutSyncRing extends MsgSyncRing {

	private static final long serialVersionUID = -2332409375033988829L;

	public MsgPostExecutSyncRing(Set<Integer> ids) {
		super(ids);
	}
	
	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}
}
