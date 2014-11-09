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
	private static final long serialVersionUID = 596680779647428670L;
	private int id;

	public MsgElection(int id, EGameState gameState) {
		super(gameState);
		this.id = id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	@Override
	public String msgContains() {
		return Integer.toString(id);
	}
}
