package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateRing;

import java.util.HashSet;
import java.util.Set;

import message.MsgCardWithNextPlayer;
import message.MsgReceiveToken;
import reso.Reso;
import JeuCartes.Carte;
import JeuCartes.Hand;

public class H_CardsShowGameState extends GameStateRing {

	private int countCards;
	private Set<Carte> cardsSend;

	public H_CardsShowGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);
		countCards = 0;
		cardsSend = new HashSet<>();
	}
	
	@Override
	protected boolean makePreExecuteSync() {
		return true;
	}

	@Override
	protected void execute() {
		// Nothing to do, wait start from leader
	}

	@Override
	protected void postExecute() {
		localPlayer.getHand().addAll(cardsSend);
		log("My cards : \n" + localPlayer.getHand());
		log("Their cards : \n" + otherPlayers.toStringHand());
	}

	@Override
	public void receive(MsgCardWithNextPlayer message) {
		getFrom(message).getHand().add(message.getCard());
		++countCards;
		if (countCards == Hand.nbCardPerPlayer * (otherPlayers.size() + 1))
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
		Carte carte = localPlayer.getHand().pollRandomCard();
		cardsSend.add(carte);
		sendToOthers(new MsgCardWithNextPlayer(carte, localPlayer.getNextPlayer().getName()));
		++countCards;
		if (countCards == Hand.nbCardPerPlayer * (otherPlayers.size() + 1))
			notifyStepDone();
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
