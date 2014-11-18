package reso;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ResoImpl extends UnicastRemoteObject implements Reso {

	private class Message {

		public final int id;
		public final String from;
		public final String to;
		public final Serializable content;

		public Message(int id, String from, String to, Serializable content) {
			this.id = id;
			this.from = from;
			this.to = to;
			this.content = content;
		}

		@Override
		public String toString() {
			return "message(" + id + ") from " + from + " to " + to;
		}

	}

	private static final long serialVersionUID = 8972046147564128682L;

	private final static double MAX_DELAY_IN_SECONDS = 0.01;

	private final AtomicInteger currentMessageId;
	private final Map<String, Client> clients;
	private final ScheduledThreadPoolExecutor scheduler;
	private final Random random;

	public ResoImpl() throws RemoteException {
		super();
		this.currentMessageId = new AtomicInteger(0);
		this.clients = Collections.synchronizedMap(new HashMap<String, Client>());
		this.scheduler = new ScheduledThreadPoolExecutor(20);
		this.random = new Random(System.currentTimeMillis());
	}

	@Override
	public void declareClient(String name, Client client) throws RemoteException {
		System.err.println("[RESO] new client " + name);
		synchronized (clients) {
			clients.put(name, client);
		}
		return;
	}

	@Override
	public void removeClient(String name) throws RemoteException {
		System.err.println("[RESO] remove client " + name);
		synchronized (clients) {
			clients.remove(name);
		}
		return;
	}

	@Override
	public void resetClients() throws RemoteException {
		System.err.println("[RESO] reset clients ");
		synchronized (clients) {
			clients.clear();
		}
		return;
	}

	@Override
	public void sendMessage(String from, String to, Serializable content) throws RemoteException {

		final Message msg = new Message(currentMessageId.getAndIncrement(), from, to, content);
		int delay = 0;
		synchronized (random) {
			delay = (int) (random.nextDouble() * MAX_DELAY_IN_SECONDS * 1000);
		}
		System.out.println("[RESO] " + msg + " arrived (delay: " + delay + "ms)");

		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				Client client = null;
				synchronized (clients) {
					client = clients.get(msg.to);
				}
				if (client == null) {
					System.err.println("[RESO] error, unknown client id: " + msg.to);
					return;
				}

				try {

					client.receiveMessage(msg.from, msg.content);
					System.out.println("[RESO] " + msg + " transmitted");

				} catch (RemoteException e) {
					System.err.println("[RESO] error while sending " + msg);
					e.printStackTrace();
				}
			}
		}, delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void broadcastMessage(String from, Serializable content) throws RemoteException {

		final Message msg = new Message(currentMessageId.getAndIncrement(), from, "Broadcast", content);
		int delay = 0;
		synchronized (random) {
			delay = (int) (random.nextDouble() * MAX_DELAY_IN_SECONDS * 1000);
		}
		System.out.println("[RESO] " + msg + " arrived (delay: " + delay + "ms)");

		scheduler.schedule(new Runnable() {
			@Override
			public void run() {

				Collection<Client> clientList = null;
				synchronized (clients) {
					clientList = clients.values();
				}
				for (Client client : clientList) {

					if (client == null) {
						System.err.println("[RESO] error, unknown client id: " + msg.to);
						return;
					}

					try {

						client.receiveMessage(msg.from, msg.content);
						System.out.println("[RESO] " + msg + " transmitted");

					} catch (RemoteException e) {
						System.err.println("[RESO] error while sending " + msg);
						e.printStackTrace();
					}
				}
			}
		}, delay, TimeUnit.MILLISECONDS);
	}

}