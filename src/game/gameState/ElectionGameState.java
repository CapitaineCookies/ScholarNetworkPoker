package game.gameState;

import java.io.Serializable;
import java.rmi.RemoteException;

import game.Game;
import game.Player;
import java.util.LinkedList;
import java.util.Queue;
import message.MsgToken;

public class ElectionGameState extends GameState {

    private Queue<MsgToken> messages;

    public ElectionGameState(Game game) {
        super(game);
        messages = new LinkedList<>();
    }

    public void sendMessageToken(int id, String to) {
        try {
            game.sendMessage(game.getPlayer().getName(), to, new MsgToken(EGameState.election, id));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {

        if (msg instanceof MsgToken) {
            MsgToken msgToken = (MsgToken) msg;
            messages.add(msgToken);
        } else {
            ignoredMessage(from, msg);
        }

    }

    @Override
    public void start() {

        //initiateur
        //if (messages.isEmpty()) {
            game.getPlayer().setState(Player.EState.init);
        //}
        
        Player.EState state = game.getPlayer().getState();

        if (state == Player.EState.init) {
            Player nextPlayer = game.getNextPlayer();
            sendMessageToken(game.getPlayer().getID(), nextPlayer.getName());

            while (state != Player.EState.leader && state != Player.EState.lost) {
                //...on recoit des receive...
                if (!messages.isEmpty()) {
                    MsgToken msg = messages.poll();
                    
                    if (msg.getId() == game.getPlayer().getID()) {
                        game.getPlayer().setState(Player.EState.leader);
                    } else {
                        if (msg.getId() > game.getPlayer().getID()) {
                            if (state == Player.EState.init) {
                                game.getPlayer().setState(Player.EState.lost);
                                sendMessageToken(msg.getId(), nextPlayer.getName());
                            }
                        }
                    }
                }
            }
        } else {
            // recieve
            // send
        }

        System.out.println(game.getPlayer().getName() + " state = " + game.getPlayer().getState());
        waitStepDone();

        goToNextStep();
    }

    @Override
    protected void goToNextStep() {
        game.setCurrentGameState(EGameState.getPlayers);
    }

}
