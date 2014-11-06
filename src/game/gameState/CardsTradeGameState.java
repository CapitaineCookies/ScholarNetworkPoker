package game.gameState;

import game.Game;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import message.MsgCard;
import message.MsgTradeCards;
import JeuCartes.Carte;
import JeuCartes.Hand;
import JeuCartes.JeuCartes;

public class CardsTradeGameState extends GameState {

	JeuCartes deck;
	Object waitRecv;

	public CardsTradeGameState(Game game) {
		super(game);
		this.deck = null;
	}

	public void setDeck(JeuCartes deck) {
		this.deck = deck;
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		if (msg instanceof MsgTradeCards)
			receiveTradeCards(from, (MsgTradeCards) msg);
		else if (msg instanceof MsgCard)
			receiveCard((MsgCard) msg);
		ignoredMessage(from, msg);
	}

	private void receiveTradeCards(String from, MsgTradeCards msg) {
		int nbCardsTarde = msg.getCards().size();

		if (!game.isLeader())
			throw new RuntimeException();

                synchronized(deck) {
                    // Add cards trade to deck
                    for (Carte carte : msg.getCards()) {
                            deck.ajoutCarte(carte);
                    }

                    // Give new cards
                    for (int i = 0; i < nbCardsTarde; ++i)
                            sendCard(from, deck.nvlleCarte());
                }

	}

	// Same in cardsDistrib
	private void sendCard(String player, Carte card) {
		try {
			game.sendMessage(player, new MsgCard(card, EGameState.cardsDistribution));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void receiveCard(MsgCard msgCard) {
		game.getPlayer().getHand().add(msgCard.getCard());
		if(game.getPlayer().getHand().getSize() == Hand.nbCardPerPlayer) {
			waitRecv.notify();
		}
			
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
			waitRecev();
		}
	}

	private void waitRecev() {
		synchronized (waitRecv) {
			try {
				waitRecv.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		System.out.println("New Cards : " + game.getPlayer().getHand());
		game.setCurrentGameState(EGameState.exit);

	}

	@Override
	public EGameState getEnum() {
		return EGameState.cardsTrade;
	}

}
