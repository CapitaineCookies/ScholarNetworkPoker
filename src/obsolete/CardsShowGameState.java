package obsolete;

import game.Game;
import game.gameState.GameState;
import game.gameState.GameState.EGameState;

import java.io.Serializable;
import java.rmi.RemoteException;

import JeuCartes.Hand;
import message.MsgCardWithNextPlayer;

public class CardsShowGameState extends GameState {

	int countCard;
	public CardsShowGameState(Game game) {
		super(game);
	}

	public boolean isInitiator() {
		return game.isLeader();
	}

	@Override
	public synchronized void receiveMessage(String from, Serializable msg) throws RemoteException {
		if (msg instanceof MsgCardWithNextPlayer)
			receiveMsgCard(from, (MsgCardWithNextPlayer) msg);
		else
			ignoredMessage(from, msg);
	}

	private synchronized void receiveMsgCard(String from, MsgCardWithNextPlayer msg) {
		// TODO synchronized
		countCard ++;
		if(countCard == Hand.nbCardPerPlayer * (game.getOtherplayers().size() + 1))
			notifyStepDone();
		else if(game.getPlayer().equals(msg.getNextPlayer()))
			sendNextShowCard();
	}

	@Override
	public void start() {
		countCard = 0;
		showCards();
		waitStepDone();
		goToNextStep();
	}

	private void showCards() {
		if (game.isLeader())
			sendNextShowCard();
	}

	private void sendNextShowCard() {
		try {
			game.broadcastMessage(new MsgCardWithNextPlayer(game.getPlayer().getHand().pollRandomCard(), game.getNextPlayer().getName(), getEGameState()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void goToNextStep() {
		game.setCurrentGameState(EGameState.exit);
	}

	@Override
	public EGameState getEGameState() {
		return EGameState.cardsShow;
	}

}
