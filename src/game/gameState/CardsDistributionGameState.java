package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;

import message.MsgCard;
import message.MsgStepDone;
import JeuCartes.Carte;
import JeuCartes.Hand;
import JeuCartes.JeuCartes;

public class CardsDistributionGameState extends GameState {

    
    JeuCartes deck;

    public CardsDistributionGameState(Game game) {
        super(game);
        this.deck = null;
    }

    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {
        if (msg instanceof MsgCard) {
            receiveCard((MsgCard) msg);
        } else if (msg instanceof MsgStepDone) {
            receiveStepDone(from);
        } else {
            ignoredMessage(from, msg);
        }

    }

    // Same in CardsTradsGameState
    private void receiveCard(MsgCard msgCard) {
        game.getPlayer().getHand().add(msgCard.getCard());
        
        if(game.getPlayer().getHand().getSize() == Hand.nbCardPerPlayer) {
            notifyStepDone();
        }
    }

    @Override
    public void start() {
        if (game.isLeader()) {
            System.out.println("Leader !");
            getDeck();
            makeDistribution();
        } else {
            System.out.println("Not Leader !");
        }

        waitStepDone();
        goToNextStep();
    }

    @Override
    protected void goToNextStep() {
        for (Player p : game.getOtherplayer()) {
            sendMsgStepDone(p.getName(), EGameState.cardsDistribution);
        }

        waitOtherPlayersDone();

        System.out.println(">>>>>>>>>>>>>>> " + game.getPlayer().getHand());
        game.setCurrentGameState(EGameState.cardsTrade);

    }

    private void getDeck() {
        deck = new JeuCartes();
        ((CardsTradeGameState) game.getGameState(EGameState.cardsTrade)).setDeck(deck);
    }

    private void makeDistribution() {
        for (int i = 0; i < Hand.nbCardPerPlayer; ++i) {
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

    @Override
    public EGameState getEGameState() {
        return EGameState.cardsDistribution;
    }

}
