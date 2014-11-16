package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateRing;
import message.MsgCardWithNextPlayer;
import message.MsgReceiveToken;
import reso.Reso;
import JeuCartes.Hand;

public class H_CardsShowGameState extends GameStateRing {

	private int countCards;

	public H_CardsShowGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);
		countCards = 0;
	}

	@Override
	protected void execute() {
		// Nothing to do, wait start from leader
	}

	@Override
	protected void postExecute() {
		log("My cards : " + localPlayer.getHand());
		log("Their cards : " + otherPlayers.toStringHand());
	}

	@Override
	public void receive(MsgCardWithNextPlayer message) {
		getFrom(message).getHand().add(message.getCard());
		++countCards;
		if (countCards == Hand.nbCardPerPlayer * otherPlayers.size())
			notifyStepDone();
		else if (localPlayer.equals(message.getNextPlayer())) {
			sendToNext(new MsgReceiveToken(localPlayer.getID()));
		}
	}

	@Override
	public void receive(MsgReceiveToken message) {
		if (message.getSenderID() == localPlayer.getID())
			sendNextShowCard();
		else
			sendToNext(message);
	}

	protected void sendNextShowCard() {
		sendToOthers(new MsgCardWithNextPlayer(localPlayer.getHand().pollRandomCard(), localPlayer.getNextPlayer().getName()));
	}

	@Override
	public EGameState getGameState() {
		return EGameState.H_cardsShow;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.Z_exit;
	}

}
