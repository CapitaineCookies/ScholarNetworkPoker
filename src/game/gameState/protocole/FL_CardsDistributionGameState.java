package game.gameState.protocole;

import reso.Reso;
import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import message.MsgCard;
import JeuCartes.Hand;
import JeuCartes.JeuCartes;

public class FL_CardsDistributionGameState extends F_CardsDistributionGameState {

	private JeuCartes deck;

	public FL_CardsDistributionGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);
	}

	@Override
	protected void preExecute() {
		createDeck();
		getDeck();
	}

	private void createDeck() {
		deck = new JeuCartes();
	}

	@Override
	protected void execute() {
		makeDistribution();
	}

	public JeuCartes getDeck() {
		return deck;
	}

	private void makeDistribution() {
		log("[Leader][Distrib]");
		for (int i = 0; i < Hand.nbCardPerPlayer; ++i) {
			for (Player otherPlayer : otherPlayers.getPlayers()) {
				send(otherPlayer, new MsgCard(deck.nvlleCarte()));
			}
			send(localPlayer, new MsgCard(deck.nvlleCarte()));
		}
	}

}
