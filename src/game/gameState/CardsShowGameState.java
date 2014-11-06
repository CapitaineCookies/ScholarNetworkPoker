package game.gameState;

import game.Game;

import java.io.Serializable;
import java.rmi.RemoteException;

import message.MsgCard;

public class CardsShowGameState extends GameState {

	public CardsShowGameState(Game game) {
		super(game);
	}

	public boolean isInitiator() {
		return game.isLeader();
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		if (msg instanceof MsgCard)
			receiveMsgCard(from, (MsgCard) msg);
		ignoredMessage(from, msg);
	}

	private void receiveMsgCard(String from, MsgCard msg) {
		game.getPlayer(from);
		if (from.equals(game.getPreviousPlayer()) && game.getPlayer().getHand().isEmpty())
			sendNextShowCard();
	}

	@Override
	public void start() {
		showCards();
		waitStepDone();
		waitOtherPlayersDone();

	}

	private void showCards() {
		if (game.isLeader())
			sendNextShowCard();
	}

	private void sendNextShowCard() {
		try {
			game.sendMessageToOther(new MsgCard(game.getPlayer().getHand().pollRandomCard(), EGameState.cardsShow));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void goToNextStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public EGameState getEnum() {
		return EGameState.cardsShow;
	}

}
