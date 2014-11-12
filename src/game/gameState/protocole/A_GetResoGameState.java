package game.gameState.protocole;

import game.Game;
import game.LocalPlayer;
import game.gameState.GameStateStandard;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import reso.Reso;

public class A_GetResoGameState extends GameStateStandard {

	Game game;
	public A_GetResoGameState(LocalPlayer localPlayer, Game game) {
		super(null,localPlayer,null);
		this.game = game;
	}

	@Override
	public void execute() {
		try {
			game.setReso((Reso) Naming.lookup(Reso.NAME));
			notifyStepDone();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public EGameState getGameState() {
		return EGameState.A_getReso;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.B_declaration;
	}
}
