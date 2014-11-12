package message;

public class MsgResolveConflict extends Message {

	private static final long serialVersionUID = -991847794677475404L;

	private double weight;

	public MsgResolveConflict(double weight) {
		super();
		this.weight = weight;
	}

	public MsgResolveConflict() {
		this(Math.random());
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String msgContains() {
		return "weight : " + weight;
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MsgResolveConflict)
			return ((MsgResolveConflict) obj).weight == weight;
		else if(obj instanceof Integer)
			return obj.equals(weight);
		return false;
	}

}
