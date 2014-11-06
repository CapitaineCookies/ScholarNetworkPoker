package game.gameState;

import java.io.Serializable;
import java.rmi.RemoteException;

import game.Game;
import game.Player;
import java.util.LinkedList;
import java.util.Queue;
import message.MsgElection;
import message.MsgLeader;
import message.MsgStepDone;

public class ElectionGameState extends GameState {

    private boolean participant;

    public ElectionGameState(Game game) {
        super(game);
        participant = false;
    }

    public void sendMessageElection(int id, String to) {
        try {
            game.sendMessage(game.getPlayer().getName(), to, new MsgElection(EGameState.election, id));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageLeader(Player p, String to) {
        try {
            game.sendMessage(game.getPlayer().getName(), to, new MsgLeader(EGameState.election, p));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {

        if (msg instanceof MsgElection) {
            MsgElection msgElection = (MsgElection) msg;

            if (msgElection.getId() > game.getPlayer().getID()) {
                sendMessageElection(msgElection.getId(), game.getNextPlayer().getName());
            } else if (msgElection.getId() == game.getPlayer().getID()) {
                System.out.println(game.getPlayer().getName() + " leader = " + game.getPlayer().getName());
                sendMessageLeader(game.getPlayer(), game.getNextPlayer().getName());
                notifyStepDone();
            } else if (msgElection.getId() < game.getPlayer().getID() && !participant) {
                participant = true;
            }

        } else if (msg instanceof MsgLeader) {
            MsgLeader msgLeader = (MsgLeader) msg;
            game.setLeader(msgLeader.getLeader());

            if (game.getPlayer().getID() != msgLeader.getLeader().getID()) {
                sendMessageLeader(msgLeader.getLeader(), game.getNextPlayer().getName());
                System.out.println(game.getPlayer().getName() + " leader = " + msgLeader.getLeader().getName());
            }
        } else if (msg instanceof MsgStepDone) {
            super.receiveStepDone(from);
        } else {
            ignoredMessage(from, msg);
        }
    }

    @Override
    public void start() {

        participant = true;
        sendMessageElection(game.getPlayer().getID(), game.getNextPlayer().getName());

        waitStepDone();
        goToNextStep();
    }

    @Override
    protected void goToNextStep() {
        for (Player p : game.getOtherplayer()) {
            sendMsgStepDone(p.getName(), EGameState.election);
        }

        waitOtherPlayersDone();
        game.setCurrentGameState(EGameState.exit);
    }

}
