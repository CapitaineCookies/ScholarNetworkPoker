package game.gameState;

import java.io.Serializable;
import java.rmi.RemoteException;

import game.Game;

public class PlayerDeclarationGameState extends GameState {

	public PlayerDeclarationGameState(Game game) {
		super(game);
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		ignoredMessage(from, msg);
	}

	@Override
	public void start() {
		game.declarePlayer();
		goToNextStep();
	}

	@Override
	protected void goToNextStep() {
		game.setCurrentGameState(EGameState.getOthers);
	}

	@Override
	public EGameState getEGameState() {
		return EGameState.declaration;
	}
}
