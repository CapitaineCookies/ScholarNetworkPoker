package game.gameState.protocole;

import game.LocalPlayer;
import game.gameState.GameStateRing;
import reso.Reso;

public class Z_ExitGameState extends GameStateRing {

	public Z_ExitGameState(Reso reso, LocalPlayer localPlayer) {
		super(reso, localPlayer, null, null);
	}

	@Override
	protected void execute() {
		disconnectPlayer();
		notifyStepDone();
	}

	@Override
	public EGameState getGameState() {
		return EGameState.Z_exit;
	}

	@Override
	public EGameState getNextState() {
		log("Unexist other nextState ! Probably need to check current state");
		return null;
	}

}
