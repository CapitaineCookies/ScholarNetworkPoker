package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import message.MsgPlaying;
import message.MsgPlayingToo;
import message.MsgPlayers;
import message.MsgStepDone;

public class GetPlayersGameState extends GameState {

    private List<Player> playersWantToPlay;
    private ScheduledThreadPoolExecutor scheduler;
    private boolean recieveplayers;
    private int nbMessageStepDone;

    public GetPlayersGameState(Game game) {
        super(game);
        this.recieveplayers = false;
        this.playersWantToPlay = new Vector<Player>();
        this.scheduler = new ScheduledThreadPoolExecutor(1);
        nbMessageStepDone = 0;
    }

    @Override
    public synchronized void receiveMessage(String from, Serializable msg) throws RemoteException {

        // TODO synchronized
        if (msg instanceof MsgPlayers && !recieveplayers) {
            recieveplayers = true;
            MsgPlayers msgPlayers = (MsgPlayers) msg;
            playersWantToPlay = msgPlayers.getPlayers();
            scheduler.shutdown();
            notifyStepDone();
        } else if (msg instanceof MsgPlaying) {
            MsgPlaying msgIPlay = (MsgPlaying) msg;
            playersWantToPlay.add(new Player(from));
            if (!getPlayerName().equals(from)) {
                game.sendMessage(getPlayerName(), from, new MsgPlayingToo(EGameState.getPlayers));
            }

        } else if (msg instanceof MsgPlayingToo) {
            if (!game.containPlayer(from)) {
                playersWantToPlay.add(new Player(from));
            }

        } else if (msg instanceof MsgStepDone) {
            nbMessageStepDone++;
            System.out.println("nbMessageStepDone=" + nbMessageStepDone + "/" + game.getOtherplayer().size());
            if (nbMessageStepDone == game.getOtherplayer().size()) {
                notifyOtherPlayersDone();
                System.out.println("Is notify");
            }

        } else {
            ignoredMessage(from, msg);
        }
    }

    @Override
    public void start() {
        sendPlayingMessage();
        startChrono();
        waitStepDone();
        goToNextStep();
    }

    private void startChrono() {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    game.broadcastMessage(game.getPlayer().getName(), new MsgPlayers(playersWantToPlay, EGameState.getPlayers));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 20, TimeUnit.SECONDS);
    }

    private void sendPlayingMessage() {
        String playerName = game.getPlayer().getName();
        try {
            game.broadcastMessage(playerName, new MsgPlaying(EGameState.getPlayers));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void goToNextStep() {
        // Check if my name is include in the list of player
        if (playersWantToPlay.contains(game.getPlayer())) {
            // Set other player list without me
            playersWantToPlay.remove(game.getPlayer());
            game.setOtherPlayer(playersWantToPlay);

            for (Player p : game.getOtherplayer()) {
                sendMsgStepDone(p.getName(), EGameState.getPlayers);
            }

            while (nbMessageStepDone < game.getOtherplayer().size()) {
                int i = 0;
            }

            /*if(nbMessageStepDone < game.getOtherplayer().size()) {
             System.out.println("Wait");
             waitOtherPlayersDone(); 
                
             } else {
             System.out.println("notWait");
             }*/
            game.setCurrentGameState(EGameState.distribNumber);

        } else {
            // Player arrived too late
            game.setCurrentGameState(EGameState.exit);
        }
    }
}
