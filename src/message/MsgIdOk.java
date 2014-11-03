package message;

import game.gameState.GameState.EGameState;

public class MsgIdOk extends Message {

    private static final long serialVersionUID = -4075650665976010262L;

    public MsgIdOk(EGameState gameState) {
        super(gameState);
    }

}
