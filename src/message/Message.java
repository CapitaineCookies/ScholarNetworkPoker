package message;

import java.io.Serializable;

public abstract class Message implements Serializable {
	
	private static final long serialVersionUID = 880439679422242011L;

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
