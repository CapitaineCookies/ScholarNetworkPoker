package main;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.List;

import reso.Reso;
import reso.ResoImpl;


public class Server {
//	private static String myAdresse;
	public static void main(String[] args) {

		try {
			// Change address
			String myAddress="";
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()){
				List<InterfaceAddress> i = en.nextElement().getInterfaceAddresses();			
			    for (InterfaceAddress l : i) {
			    	InetAddress adr = l.getAddress();
			        if  (adr.isSiteLocalAddress()){
			           myAddress=adr.getHostAddress();
			        }
			    }
			}
			System.setProperty("java.rmi.server.hostname", myAddress);

			Registry registry = LocateRegistry.createRegistry(1099);
			
			Reso reso = new ResoImpl();
			registry.rebind(Reso.NAME, reso);
			
			System.out.println("Reso successfully launched!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
