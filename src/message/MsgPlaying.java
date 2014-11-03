package message;

import game.gameState.GameState.EGameState;

public class MsgPlaying extends Message {

    private static final long serialVersionUID = -3878682244225160047L;
    String playerName;

    public MsgPlaying(EGameState gameState) {
        super(gameState);
    }
}
