package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateStandard;
import message.MsgCard;
import reso.Reso;
import JeuCartes.Hand;

public class F_CardsDistributionGameState extends GameStateStandard {

	public F_CardsDistributionGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);
	}

	@Override
	protected void execute() {
		System.out.println("[NotLeader][WaitDistrib]");
	}

	@Override
	protected boolean makePrePostExecuteSynchro() {
		return true;
	}

	@Override
	public void receive(MsgCard message) {
		localPlayer.getHand().add(message.getCard());

		if (localPlayer.getHand().getSize() == Hand.nbCardPerPlayer) {
			notifyStepDone();
		}
	}

	@Override
	public EGameState getGameState() {
		return EGameState.F_cardsDistribution;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.G_cardsTrade;
	}

}
