package message;

import game.RemotePlayer;
import game.gameState.GameState.EGameState;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MsgPlayers extends Message {

    private static final long serialVersionUID = 5477700306721984176L;
    Collection<String> players;

    public MsgPlayers(Collection<String> players, EGameState gameState) {
        super(gameState);
        this.players = players;
    }

    public Set<RemotePlayer> getPlayers() {
    	Set<RemotePlayer> remotePlayers = new HashSet<RemotePlayer>(players.size());
    	for(String player : players)
    		remotePlayers.add(new RemotePlayer(player));
        return remotePlayers;
    }

    public void setPlayers(Collection<String> players) {
        this.players = players;
    }
    
    @Override
    public String msgContains() {
    	return players.toString();
    }

}
