package reso.example;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import reso.Client;
import reso.Reso;

public class Sender extends UnicastRemoteObject implements Client {

	private static final long serialVersionUID = -3879530234484702428L;

	protected Sender() throws RemoteException {
		super();
	}

	@Override
	public void receiveMessage(String from, Serializable msg)
			throws RemoteException {
		System.out.println("Sender received a message from " + from + ": " + msg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Reso reso = (Reso) Naming.lookup(Reso.NAME);
			
			String nameSender = new String("nS");
			reso.declareClient(nameSender, new Sender());
			System.out.println("Sender has name: " + nameSender);
			
			for (int i = 0; i < 10; i++) {
				reso.sendMessage(nameSender, "nR", "Hello you!");
			}
			
			reso.broadcastMessage("nR", "Diffusion");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
