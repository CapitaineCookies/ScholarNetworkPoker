package game.gameState.protocole;

import game.LocalPlayer;
import game.gameState.GameStateStandard;
import reso.Reso;

public class Z_ExitGameState extends GameStateStandard {

	
	public Z_ExitGameState(Reso reso, LocalPlayer localPlayer) {
		super(reso, localPlayer, null);
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
		throw new RuntimeException("Unexist other nextState ! Probably need to check current state");
	}

}
