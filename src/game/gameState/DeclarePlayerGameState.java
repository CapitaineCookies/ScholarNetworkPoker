package game.gameState;

import java.io.Serializable;
import java.rmi.RemoteException;

import game.Game;

public class DeclarePlayerGameState extends GameState {

	public DeclarePlayerGameState(Game game) {
		super(game);
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		ignoredMessage(from, msg);
	}

	@Override
	public void start() {
		System.out.println(toString() + " start");
		game.declarePlayer();
		goToNextStep();
	}

	@Override
	protected void goToNextStep() {
		game.setCurrentGameState(EGameState.getPlayers);
	}

	@Override
	public EGameState getEnum() {
		return EGameState.declarePlayer;
	}
}
