package game.gameState;

import java.io.Serializable;
import java.rmi.RemoteException;

import game.Game;

public class ExitGameState extends GameState {

	public ExitGameState(Game game) {
		super(game);
	}

	@Override
	public void start() {
		goToNextStep();
	}

	@Override
	protected void goToNextStep() {
		// Non next step
		// TODO se retirer du reso
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		ignoredMessage(from, msg);
	}
	

}
