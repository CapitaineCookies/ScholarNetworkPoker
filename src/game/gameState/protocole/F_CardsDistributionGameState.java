package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateStandard;
import message.MsgCard;
import reso.Reso;
import JeuCartes.Hand;
import JeuCartes.JeuCartes;

public class F_CardsDistributionGameState extends GameStateStandard {

	JeuCartes deck;
	G_TradeCardsGameState nextGameState;

	public F_CardsDistributionGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader, G_TradeCardsGameState nextGameState) {
		super(reso, localPlayer, otherPlayers, leader);
		this.nextGameState = nextGameState;
	}

	@Override
	protected void preExecute() {
		if (isLeader()) {
			System.out.println("[Leader][Deck]");
			getDeck();
		} else {
			System.out.println("[NotLeader][Deck]");
		}
	}

	@Override
	protected void execute() {
		if (isLeader()) {
			System.out.println("[Leader][Distrib]");
			makeDistribution();
		}
		else {
			System.out.println("[NotLeader][Distrib]");
		}
	}

	@Override
	protected boolean makePrePostExecuteSynchro() {
		return true;
	}

	@Override
	public void receive(MsgCard message) {
		localPlayer.getHand().add(message.getCard());

		if (localPlayer.getHand().getSize() == Hand.nbCardPerPlayer) {
			notifyStepDone();
		}
	}

	private void getDeck() {
		deck = new JeuCartes();
		nextGameState.setDeck(deck);
	}

	private void makeDistribution() {
		for (int i = 0; i < Hand.nbCardPerPlayer; ++i) {
			for (Player otherPlayer : otherPlayers.getPlayers()) {
				send(otherPlayer, new MsgCard(deck.nvlleCarte()));
			}
			send(localPlayer, new MsgCard(deck.nvlleCarte()));
		}
	}

	@Override
	public EGameState getGameState() {
		return EGameState.F_cardsDistribution;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.G_cardsTrade;
	}

}
