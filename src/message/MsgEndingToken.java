package message;

import java.util.HashMap;
import java.util.Map;

public class MsgEndingToken extends Message {

	private static final long serialVersionUID = 7632097775670012767L;

	private boolean endingToken;
	private Map<String, Integer> lastCount;
	private int counter;

	public MsgEndingToken() {
		endingToken = false;
		counter = 0;
		lastCount = new HashMap<>();
	}

	@Override
	public String msgContains() {
		String s = (endingToken) ? "valid" : "notValid";
		return s + " | counter = " + counter + " | lastCount = " + lastCount;
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.receive(this);
	}

	public void invalidToken(String name, int nbInvalid) {
		if (nbInvalid == 0)
			return;

		counter += nbInvalid;
		endingToken = false;
		if (lastCount.containsKey(name))
			lastCount.put(name, lastCount.get(name) + nbInvalid);
		else
			lastCount.put(name, nbInvalid);

	}

	public int takeNbTradeMade(String name) {
		int playerVal = (lastCount.containsKey(name)) ? lastCount.get(name) : 0;
		int result = counter - playerVal;
		lastCount.put(name, counter);
		return result;
	}

	public boolean isValid() {
			return endingToken;
	}

	public void initialiseValid() {
		endingToken = true;
	}
}
