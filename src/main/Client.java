/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import game.Game;
import game.LocalPlayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 *
 * @author rgrimmer
 */
public class Client {
	private static String myAdresse;
    	public static void main(String[] args) throws Exception {
		/*int NB_PLAYERS = 20;
		Game players[] = new Game[NB_PLAYERS];
		Thread t[] = new Thread[NB_PLAYERS];
		for (int i = 0; i < NB_PLAYERS; ++i) {
			players[i] = new Game(new Player("p" + i));
			t[i] = new Thread(players[i]);
			t[i].start();
		}*/
    		// First change address
		    Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
		    while (en.hasMoreElements()){
		     
		      List<InterfaceAddress> i = en.nextElement().getInterfaceAddresses();
		      for (InterfaceAddress l : i) {

		        InetAddress adr = l.getAddress();
		        if  (adr.isSiteLocalAddress()){
		           myAdresse=adr.getHostAddress();
		        }
		      }
		    }		    
		    System.setProperty("java.rmi.server.hostname", myAdresse);
		    System.out.println(myAdresse);

	    	Game p = new Game(new LocalPlayer("p" + (int)(Math.random() * 1000)));
	    	p.startGame();
    		
//            p.startGame();
            System.out.println("fin du precessus client !");
	}
}
