package game.gameState;

import game.Game;

import java.io.Serializable;
import java.rmi.RemoteException;

import message.MsgCard;

public class ShowCardsGameState extends GameState {
	
	public ShowCardsGameState(Game game) {
		super(game);
	}
	
	public boolean isInitiator() {
		return game.isLeader();
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		showCards();
		waitStepDone();
		waitOtherPlayersDone();

	}

	private void showCards() {
		if(game.isLeader())
			sendNextShowCard();
	}

	private void sendNextShowCard() {
		try {
			game.sendMessageToOther(new MsgCard(game.getPlayer().getHand().pollRandomCard(), EGameState.showCards));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void goToNextStep() {
		// TODO Auto-generated method stub

	}

}
