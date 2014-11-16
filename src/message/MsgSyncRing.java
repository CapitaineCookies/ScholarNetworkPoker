package message;

import java.util.Set;

public abstract class MsgSyncRing extends Message {
	
	private static final long serialVersionUID = -5432159121162376589L;

	Set<Integer> ids;
	
	public MsgSyncRing(Set<Integer> ids) {
		this.ids = ids;
	}
	
	public Set<Integer> getIDs() {
		return ids;
	}

	@Override
	public String msgContains() {
		return ids.toString();
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

}
