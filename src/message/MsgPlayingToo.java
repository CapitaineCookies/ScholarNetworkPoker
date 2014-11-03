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
public class MsgPlayingToo extends Message {

    public MsgPlayingToo(EGameState eGameState) {
        super(eGameState);
    }
    
}
