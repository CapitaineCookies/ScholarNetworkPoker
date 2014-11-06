package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;

import message.MsgCard;
import message.MsgStepDone;
import message.MsgTradeCards;
import JeuCartes.Carte;
import JeuCartes.JeuCartes;

public class CardsDistributionGameState extends GameState {

	private static final int nbCardToDistribPerPlayer = 5;
	
	JeuCartes deck;

	public CardsDistributionGameState(Game game) {
		super(game);
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
		
		if (!game.isLeader())
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
		game.getPlayer().getHand().add(msgCard.getCard());
	}

	@Override
	public void start() {
		if (game.isLeader()) {
			getDeck();
			makeDistribution();
		}

		waitOtherPlayersDone();
		goToNextStep();
	}

	@Override
	protected void goToNextStep() {
		game.setCurrentGameState(EGameState.cardsTrade);

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

}
