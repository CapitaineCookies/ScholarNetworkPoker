package game.gameState;

import game.Game;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import message.MsgTradeCards;
import JeuCartes.Carte;

public class CardsTradeGameState extends GameState {

	public CardsTradeGameState(Game game) {
		super(game);
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {

		tradeHisCards();

		waitStepDone();
		waitOtherPlayersDone();
		goToNextStep();
	}

	private void tradeHisCards() {

		int nbExchange = (int) Math.random() * 4; // 0, 1, 2 or 3
		for (; nbExchange < 0; --nbExchange) {
			int nbCardTrad = (int) Math.random() * 5 + 1;
			sendTardeCards(game.getPlayer().getHand().getRandomCards(nbCardTrad), EGameState.cardsDistribution);
		}
	}

	private void sendTardeCards(List<Carte> cards, EGameState cardsdistribution) {
		try {
			game.sendMessage(game.getLeader(), new MsgTradeCards(cards, EGameState.cardsDistribution));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void goToNextStep() {
		// TODO Auto-generated method stub

	}

}
