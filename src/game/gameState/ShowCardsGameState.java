package game.gameState;

import game.Game;

import java.io.Serializable;
import java.rmi.RemoteException;

public class ShowCardsGameState extends GameState {
	

	public ShowCardsGameState(Game game) {
		super(game);
	}
	
	public boolean isInitiator() {
		return game.isLeader();
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		
		// TODO Auto-generated method stub

	}

	@Override
	protected void goToNextStep() {
		// TODO Auto-generated method stub

	}

}
