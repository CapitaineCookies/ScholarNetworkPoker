package reso.example;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import reso.Client;
import reso.Reso;

public class Receiver extends UnicastRemoteObject implements Client {

	private static final long serialVersionUID = 842940924381059064L;

	protected Receiver() throws RemoteException {
		super();
	}

	@Override
	public void receiveMessage(String from, Serializable msg)
			throws RemoteException {
		System.out.println("Receiver received a message from " + from + ": " + msg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Reso reso = (Reso) Naming.lookup(Reso.NAME);
			
			String nameReceiver = new String("nR");
			reso.declareClient(nameReceiver, new Receiver());
			System.out.println("Receiver with name " + nameReceiver);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
