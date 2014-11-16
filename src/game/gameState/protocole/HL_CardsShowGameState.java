package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import reso.Reso;

public class HL_CardsShowGameState extends H_CardsShowGameState {

	public HL_CardsShowGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);
	}

	@Override
	protected void execute() {
		sendNextShowCard();
	}
}
