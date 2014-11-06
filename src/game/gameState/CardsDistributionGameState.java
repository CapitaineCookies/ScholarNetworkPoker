package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import message.MsgCard;
import message.MsgTrandCards;
import JeuCartes.Carte;
import JeuCartes.Hand;
import JeuCartes.JeuCartes;

public class CardsDistributionGameState extends GameState {

	private static final int nbCardToDistribPerPlayer = 5;
	private Hand hand;
	private Player master;
	JeuCartes deck = null;
	private boolean isMaster;

	public void setMaster(Player master) {
		this.master = master;
	}

	private boolean isMaster() {
		return master == game.getPlayer();
	}

	public CardsDistributionGameState(Game game) {
		super(game);
		hand = new Hand();
		isMaster = false;
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		if (msg instanceof MsgTrandCards) {
			if (!isMaster())
				throw new RuntimeException();

		}
	}

	public void receiveCard(MsgCard msgCard) {
		hand.add(msgCard.getCard());
	}

	@Override
	public void start() {
		if (isMaster()) {
			getDeck();
			makeDistribution();
		}

		changeCards();

		waitStepDone();
		waitOtherPlayersDone();
		goToNextStep();
	}

	private void getDeck() {
		deck = new JeuCartes();
	}

	private void makeDistribution() {
		for (int i = 0; i < nbCardToDistribPerPlayer; ++i) {
			for (Player otherPlayer : game.getOtherplayer()) {
				sendCard(deck.nvlleCarte(), otherPlayer);
			}
			sendCard(deck.nvlleCarte(), game.getPlayer());
		}
	}

	private void sendCard(Carte card, Player player) {
		try {
			game.sendMessage(player, new MsgCard(card, EGameState.cardsDistribution));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void changeCards() {

		int nbExchange = (int) Math.random() * 4; // 0, 1, 2 or 3
		for (; nbExchange < 0; --nbExchange) {
			int nbCardTrad = (int) Math.random() * 5 + 1;
			sendTardCards(hand.getRandomCards(nbCardTrad), EGameState.cardsDistribution);
		}
	}

	private void sendTardCards(List<Carte> cards, EGameState cardsdistribution) {
		try {
			game.sendMessage(master, new MsgTrandCards(cards, EGameState.cardsDistribution));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void goToNextStep() {
		// TODO Auto-generated method stub

	}

}
