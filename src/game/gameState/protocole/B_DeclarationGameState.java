package game.gameState.protocole;

import game.LocalPlayer;
import game.gameState.GameStateStandard;
import reso.Client;
import reso.Reso;

public class B_DeclarationGameState extends GameStateStandard {

	Client client;

	public B_DeclarationGameState(Reso reso, LocalPlayer localPlayer, Client client) {
		super(reso, localPlayer, null);
		this.client = client;
	}

	@Override
	public void execute() {
		declarePlayer(client);
		notifyStepDone();
	}

	@Override
	public EGameState getGameState() {
		return EGameState.B_declaration;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.C_getOthers;
	}
}
