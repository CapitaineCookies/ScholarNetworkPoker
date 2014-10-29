/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import game.Game;
import game.Player;

/**
 *
 * @author rgrimmer
 */
public class Client {
    	public static void main(String[] args) throws Exception {
		int NB_PLAYERS = 20;
		Game players[] = new Game[NB_PLAYERS];
		Thread t[] = new Thread[NB_PLAYERS];
		for (int i = 0; i < NB_PLAYERS; ++i) {
			players[i] = new Game(new Player("p" + i));
			t[i] = new Thread(players[i]);
			t[i].start();
		}
	}
}
