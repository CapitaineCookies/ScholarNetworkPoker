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

	private static final long serialVersionUID = -5573026295437888232L;

	public MsgSync(EGameState gameState) {
		super(gameState);
	}

	@Override
	public String msgContains() {
		return "";
	}
}
