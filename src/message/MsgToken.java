package message;

import game.gameState.GameState.EGameState;

public class MsgToken extends Message {

    private static final long serialVersionUID = -3878682244225160047L;
    int id;

    public MsgToken(EGameState gameState, int id) {
        super(gameState);
        this.id = id;
    }
    
    public void setId(int id) {
        this.id = id;
    } 
    
    public int getId() {
        return this.id;
    }
}
