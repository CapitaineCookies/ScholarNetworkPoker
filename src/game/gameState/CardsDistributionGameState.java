package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import message.MsgCard;
import message.MsgStepDone;
import message.MsgTradeCards;
import JeuCartes.Carte;
import JeuCartes.Hand;
import JeuCartes.JeuCartes;

public class CardsDistributionGameState extends GameState {

	private static final int nbCardToDistribPerPlayer = 5;
	
	private Hand hand;
	private Player master;
	JeuCartes deck;

	public void setMaster(Player master) {
		this.master = master;
	}

	private boolean isMaster() {
		return master == game.getPlayer();
	}

	public CardsDistributionGameState(Game game) {
		super(game);
		this.hand = new Hand();
		this.master = null;
		this.deck = null;
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		if (msg instanceof MsgTradeCards)
			receiveTradeCards(from, (MsgTradeCards)msg);
		else if(msg instanceof MsgCard)
			receiveCard((MsgCard)msg);
		else if(msg instanceof MsgStepDone)
			receiveStepDone(from);
		
		ignoredMessage(from, msg);
	}

	private void receiveTradeCards(String from, MsgTradeCards msg) {
		int nbCardsTarde = msg.getCards().size();
		
		if (!isMaster())
			throw new RuntimeException();
		
		// Add cards trade to deck
		for(Carte carte : msg.getCards()) {
			deck.ajoutCarte(carte);
		}
		
		// Give new cards
		for(int i = 0; i < nbCardsTarde; ++i)
			sendCard(from, deck.nvlleCarte());
		
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

		tradeHisCards();

		waitStepDone();
		waitOtherPlayersDone();
		goToNextStep();
	}

	@Override
	protected void goToNextStep() {
		game.setCurrentGameState(EGameState.showCards);

	}

	private void getDeck() {
		deck = new JeuCartes();
	}

	private void makeDistribution() {
		for (int i = 0; i < nbCardToDistribPerPlayer; ++i) {
			for (Player otherPlayer : game.getOtherplayer()) {
				sendCard(otherPlayer, deck.nvlleCarte());
			}
			sendCard(game.getPlayer(), deck.nvlleCarte());
		}
	}

	private void sendCard(Player player, Carte card) {
		sendCard(player.getName(), card);
	}

	private void sendCard(String player, Carte card) {
		try {
			game.sendMessage(player, new MsgCard(card, EGameState.cardsDistribution));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void tradeHisCards() {

		int nbExchange = (int) Math.random() * 4; // 0, 1, 2 or 3
		for (; nbExchange < 0; --nbExchange) {
			int nbCardTrad = (int) Math.random() * 5 + 1;
			sendTardCards(hand.getRandomCards(nbCardTrad), EGameState.cardsDistribution);
		}
	}

	private void sendTardCards(List<Carte> cards, EGameState cardsdistribution) {
		try {
			game.sendMessage(master, new MsgTradeCards(cards, EGameState.cardsDistribution));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
