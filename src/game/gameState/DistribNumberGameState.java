package game.gameState;

import java.io.Serializable;
import java.rmi.RemoteException;

import game.Game;

public class DistribNumberGameState extends GameState {

	public DistribNumberGameState(Game game) {
		super(game);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void goToNextStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
