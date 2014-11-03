package message;

import game.Player;
import game.gameState.GameState.EGameState;

import java.util.List;

public class MsgPlayers extends Message {

    private static final long serialVersionUID = 5477700306721984176L;
    List<Player> players;

    public MsgPlayers(List<Player> players, EGameState gameState) {
        super(gameState);
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

}
