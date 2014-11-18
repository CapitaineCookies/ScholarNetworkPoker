package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateRing;
import message.MsgCard;
import reso.Reso;
import JeuCartes.Hand;

public class F_CardsDistribGameState extends GameStateRing {

	public F_CardsDistribGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);
	}

	@Override
	protected void execute() {
		System.out.println("[NotLeader][WaitDistrib]");
	}

	@Override
	protected boolean makePostExecuteSync() {
		return true;
	}

	@Override
	public void receive(MsgCard message) {
		synchronized (localPlayer) {

			localPlayer.getHand().add(message.getCard());

			if (localPlayer.getHand().getSize() == Hand.nbCardPerPlayer) {
				notifyStepDone();
			}
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
