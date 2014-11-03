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
public class MsgSync extends Message {
    
    public MsgSync(EGameState gameState) {
        super(gameState);
    }
}
