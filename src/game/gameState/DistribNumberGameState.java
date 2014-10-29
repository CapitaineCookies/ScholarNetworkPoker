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
import message.MsgPlayers;
import message.MsgPlaying;
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
            game.sendMessage(playerName, p, new MsgSync());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendIdOkMessage(String p) {
        String playerName = game.getPlayer().getName();
        try {
            game.sendMessage(playerName, p, new MsgIdOk());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendIdMessage(String p) {
        String playerName = game.getPlayer().getName();

        try {
            game.sendMessage(playerName, p, new MsgIdChoice(game.getPlayer().getID()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        try {
         Thread.sleep(5000);
         } catch (InterruptedException ex) {
         Logger.getLogger(DistribNumberGameState.class.getName()).log(Level.SEVERE, null, ex);
         }

        int taille = game.getOtherplayer().size() + 1;
        int id = (int) (Math.random() * taille);

        game.getPlayer().setID(id);
        //System.out.println(game.getPlayer().getName() + " has choosen ID = " + id);

        for (Player p : game.getOtherplayer()) {
            sendIdMessage(p.getName());
        }

        waitStepDone();
        goToNextStep();
    }

    @Override
    protected void goToNextStep() {
        System.out.println("goToNextStep");

        System.out.println(game.getPlayer() + " : " + game.getOtherplayer());
        game.setGameState(new ExitGameState(game));

        /*try {
         Thread.sleep(1000000000);
         } catch (InterruptedException ex) {
         Logger.getLogger(DistribNumberGameState.class.getName()).log(Level.SEVERE, null, ex);
         }*/
    }

    @Override
    public synchronized void receiveMessage(String from, Serializable msg) throws RemoteException {

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

                        for (Player p : game.getOtherplayer()) {
                            sendIdMessage(p.getName());
                        }

                        alreadySentOk = true;
                    } else {
                        for (Player p : game.getOtherplayer()) {
                            sendIdMessage(p.getName());
                        }
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

                //ne pas quitter l'état si on a pas encore envoyer OK aux autres.
                /*if (!alreadySentOk) {
                 for (Player p : game.getOtherplayer()) {
                 sendIdOkMessage(p.getName());
                 }

                 for (Player p : game.getOtherplayer()) {
                 sendIdMessage(p.getName());
                 }
                    
                 alreadySentOk = true;
                 }*/
                System.out.println("OKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
                notifyStepDone();
            }
        }

    }

    public boolean allDifferent() {

        ArrayList<Player> players = new ArrayList<>();

        players.add(game.getPlayer());
        for (Player p : game.getOtherplayer()) {
            players.add(p);
        }

        for (int i = 0; i < players.size(); ++i) {
            if (!isDifferent(players.get(i), players)) {
                return false;
            }
        }

        return true;
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
        int id = -1;

        List<Player> L = new ArrayList<>(); //contient la liste des joueurs
        List<Integer> U = new ArrayList<>(); //contient la liste des id à ne pas choisir
        List<Integer> C = new ArrayList<>(); //contient la liste des ids a choisir

        L.add(game.getPlayer());
        for (Player p : game.getOtherplayer()) {
            L.add(p);
        }

        for (int i = 0; i < game.getOtherplayer().size() + 1; ++i) {
            C.add(i);
        }

        for (int i = 0; i < L.size(); ++i) {
            if (isDifferent(L.get(i), L)) {
                U.add(L.get(i).getID());
            }
        }

        //System.out.println("Ids a ne pas choisir >>>> " + U);
        for (int i = 0; i < L.size(); ++i) {
            //ids que l'ont peut choisir
            if ((U.contains(L.get(i).getID()))) {
                C.remove((Integer) (L.get(i).getID()));
            }
        }

        //System.out.println("Ids possible >>>> " + C);
        int index = (int) (Math.random() * C.size());

        return C.get(index);
    }
}
