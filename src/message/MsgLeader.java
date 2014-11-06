/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package message;

import game.Player;
import game.gameState.GameState.EGameState;

/**
 *
 * @author rgrimmer
 */
public class MsgLeader extends Message {
    
    private Player leader;
    
    public MsgLeader(EGameState gameState, Player p) {
        super(gameState);
        this.leader = p; 
    }

    public Player getLeader() {
        return leader;
    }

    public void setLeader(Player p) {
        this.leader = p;
    }
    
    

}
