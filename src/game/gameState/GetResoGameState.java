package game.gameState;

import game.Game;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;

import reso.Reso;

public class GetResoGameState extends GameState {

	

	public GetResoGameState(Game game) {
		super(game);
	}

	@Override
	public void start() {
		System.out.println(toString() + " start");
		setReso();
		goToNextStep();
	}

	private void setReso() {
		Reso reso = null;
		try {
			reso = (Reso) Naming.lookup(Reso.NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		game.setReso(reso);
	}

	@Override
	protected void goToNextStep() {
		game.setCurrentGameState(EGameState.declarePlayer);
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		ignoredMessage(from, msg);
	}

}
