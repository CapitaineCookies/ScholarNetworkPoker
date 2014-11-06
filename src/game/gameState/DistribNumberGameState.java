package game.gameState;

import java.io.Serializable;
import java.rmi.RemoteException;

import game.Game;
import game.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.MsgIdChoice;
import message.MsgIdOk;
import message.MsgStepDone;
import message.MsgSync;

public class DistribNumberGameState extends GameState {

    int nbMsgSync;
    int nbMsgReceived;
    int nbMsgIdOk;
    boolean alreadySentOk;

    public DistribNumberGameState(Game game) {
        super(game);

        nbMsgSync = 0;
        nbMsgReceived = 0;
        alreadySentOk = false;
    }

    public void sendSynchroMessage(String p) {
        String playerName = game.getPlayer().getName();
        try {
            game.sendMessage(playerName, p, new MsgSync(EGameState.distribNumber));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendIdOkMessage(String p) {
        String playerName = game.getPlayer().getName();
        try {
            game.sendMessage(playerName, p, new MsgIdOk(EGameState.distribNumber));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendIdMessage(String p) {
        String playerName = game.getPlayer().getName();

        try {
            game.sendMessage(playerName, p, new MsgIdChoice(game.getPlayer().getID(), EGameState.distribNumber));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        //TODO: il faut que tout les processus soient prêt avant de commencer à envoyer des messages = réussir a supprimer le sleeeeep :)

        /*try {
         Thread.sleep(10000);
         } catch (InterruptedException ex) {
         Logger.getLogger(DistribNumberGameState.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
        int taille = game.getOtherplayer().size() + 1;
        int id = (int) (Math.random() * taille);

        game.getPlayer().setID(id);

        for (Player p : game.getOtherplayer()) {
            sendIdMessage(p.getName());
        }

        waitStepDone();
        goToNextStep();
    }

    @Override
    protected void goToNextStep() {
        for (Player p : game.getOtherplayer()) {
            sendMsgStepDone(p.getName(), EGameState.distribNumber);
        }

        waitOtherPlayersDone();
        System.out.println("goToNextStep");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + game.getPlayer() + " : " + game.getOtherplayer());
        game.setCurrentGameState(EGameState.election);
    }

    @Override
    public synchronized void receiveMessage(String from, Serializable msg) throws RemoteException {
        if (game.getCurrentGameState() instanceof ElectionGameState) {
            return;
        }

        if (msg instanceof MsgIdChoice) {

            MsgIdChoice msgIdChoice = (MsgIdChoice) msg;

            for (Player p : game.getOtherplayer()) {
                if (p.getName().equals(from)) {
                    p.setID(msgIdChoice.getId());
                }
            }

            nbMsgReceived++;

            if (nbMsgReceived == game.getOtherplayer().size()) {
                for (Player p : game.getOtherplayer()) {
                    sendSynchroMessage(p.getName());
                }

                nbMsgReceived = 0;
            }
        } else if (msg instanceof MsgSync) {
            nbMsgSync++;
            System.out.println(game.getPlayer().getName() + "msgIdSync : " + nbMsgSync + "/" + game.getOtherplayer().size());

            if (nbMsgSync == game.getOtherplayer().size()) {

                if (isDifferent(game.getPlayer(), game.getOtherplayer())) {

                    if (!alreadySentOk) {
                        for (Player p : game.getOtherplayer()) {
                            sendIdOkMessage(p.getName());
                        }

                        alreadySentOk = true;
                    }
                    for (Player p : game.getOtherplayer()) {
                        sendIdMessage(p.getName());
                    }

                } else {
                    int newId = chooseValidId();
                    game.getPlayer().setID(newId);
                    System.out.println(game.getPlayer().getName() + " has a new ID : " + game.getPlayer().getID());
                    for (Player p : game.getOtherplayer()) {
                        sendIdMessage(p.getName());
                    }
                }

                nbMsgSync = 0;
            }
        } else if (msg instanceof MsgIdOk) {
            nbMsgIdOk++;
            System.out.println(game.getPlayer().getName() + "msgIdOk : " + nbMsgIdOk + "/" + game.getOtherplayer().size());
            if (nbMsgIdOk == game.getOtherplayer().size()) {
                System.out.println(">>>>>>>>>>>>>>>>>>< NOTIFY STEP DONE DISTRIB NUMBER");
                notifyStepDone();
            }
        } else if (msg instanceof MsgStepDone) {
            super.receiveStepDone(from);
        } else {
            ignoredMessage(from, msg);
        }

    }

    public boolean isDifferent(Player p, List<Player> l) {

        for (int i = 0; i < l.size(); ++i) {
            if (!p.getName().equals(l.get(i).getName())) {
                if (p.getID() == l.get(i).getID()) {
                    return false;
                }
            }
        }

        return true;
    }

    public int chooseValidId() {

        List<Player> players = new ArrayList<>(); //contient la liste de tous les joueurs
        List<Integer> idsNotToChoose = new ArrayList<>(); //contient la liste des id à ne pas choisir
        List<Integer> idsToChoose = new ArrayList<>(); //contient la liste des ids à choisir

        players.add(game.getPlayer());
        for (Player p : game.getOtherplayer()) {
            players.add(p);
        }

        for (int i = 0; i < game.getOtherplayer().size() + 1; ++i) {
            idsToChoose.add(i);
        }

        for (int i = 0; i < players.size(); ++i) {
            if (isDifferent(players.get(i), players)) {
                idsNotToChoose.add(players.get(i).getID());
            }
        }

        for (int i = 0; i < players.size(); ++i) {
            if ((idsNotToChoose.contains(players.get(i).getID()))) {
                idsToChoose.remove((Integer) (players.get(i).getID()));
            }
        }

        int index = (int) (Math.random() * idsToChoose.size());
        return idsToChoose.get(index);
    }
}
