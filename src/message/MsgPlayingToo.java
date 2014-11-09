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

	private static final long serialVersionUID = -8881890658503260381L;

	public MsgPlayingToo(EGameState eGameState) {
		super(eGameState);
	}

	@Override
	public String msgContains() {
		return "";
	}
}
