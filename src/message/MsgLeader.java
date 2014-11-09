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
public class MsgLeader extends Message {

	private static final long serialVersionUID = 5028715142368247042L;

	private String leaderName;
	private int leaderID;

	public MsgLeader(String leaderName, int leaderID, EGameState gameState) {
		super(gameState);
		this.leaderName = leaderName;
		this.leaderID = leaderID;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String p) {
		this.leaderName = p;
	}

	public int getLeaderID() {
		return leaderID;
	}

	public void setLeaderID(int leaderID) {
		this.leaderID = leaderID;
	}
	
	@Override
	public String msgContains() {
		return leaderID + " " + leaderName;
	}

}
