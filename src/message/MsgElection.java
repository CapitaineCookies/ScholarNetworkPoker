/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package message;

import game.gameState.GameState.EGameState;

/**
 *
 * @author rgrimmer
 */
public class MsgElection extends Message {
    
    private int id;
    
    public MsgElection(EGameState gameState, int id) {
        super(gameState);
        this.id = id; 
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
