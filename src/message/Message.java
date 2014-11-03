package message;

import game.gameState.GameState.EGameState;
import java.io.Serializable;

public abstract class Message implements Serializable {

    private static final long serialVersionUID = 880439679422242011L;

    EGameState gameState;

    public Message(EGameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public EGameState getGameState() {
        return gameState;
    }

}
