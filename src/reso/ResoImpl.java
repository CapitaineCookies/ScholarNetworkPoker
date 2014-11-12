package reso;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ResoImpl extends UnicastRemoteObject implements Reso {

	private static class Message {

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

	private final static int MAX_DELAY_IN_SECONDS = 0;

	private final AtomicInteger currentMessageId;
	private final Map<String, Client> clients;
	private final ScheduledThreadPoolExecutor scheduler;
	private final Random random;

	public ResoImpl() throws RemoteException {
		super();
		this.currentMessageId = new AtomicInteger(0);
		this.clients = Collections.synchronizedMap(new HashMap<String, Client>());
		this.scheduler = new ScheduledThreadPoolExecutor(200);
		this.random = new Random(System.currentTimeMillis());
	}

	@Override
	public void declareClient(String name, Client client) throws RemoteException {
		clients.put(name, client);
		System.out.println("[RESO] new client declared : " + name);
	}

	@Override
	public void sendMessage(String from, String to, Serializable content) throws RemoteException {

		final Message msg = new Message(currentMessageId.getAndIncrement(), from, to, content);
		int delay = 0;
		synchronized (random) {
			delay = (int) (random.nextDouble() * MAX_DELAY_IN_SECONDS * 1000);
		}
		System.out.println("[RESO][Send] " + msg + "(delay: " + delay + "ms) : " + msg.content);

		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				Client client = clients.get(msg.to);

				if (client == null) {
					System.err.println("[RESO] error, unknown client id: " + msg.to);
					return;
				}

				try {
					System.out.println("[RESO][Recv] " + msg + " transmitted");
					client.receiveMessage(msg.from, msg.content);
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
		System.out.println("[RESO][Brod] message(" + msg.id + ") from " + msg.from + " to " + clients.keySet() + " (delay: " + delay + "ms)" + msg.content);

		scheduler.schedule(new Runnable() {
			@Override
			public void run() {

//				Collection<Client> clientList = clients.values();
				for (Entry<String, Client> client : clients.entrySet()) {

					if (client == null || client.getKey().isEmpty() || client.getValue() == null) {
						System.err.println("[RESO] error, unknown client id: " + msg.to);
						return;
					}

					try {
						System.out.println("[RESO][Recv] message(" + msg.id + ") from " + msg.from + " to " + client.getKey() + " transmitted  : " + msg.content);
						client.getValue().receiveMessage(msg.from, msg.content);
					} catch (RemoteException e) {
						System.err.println("[RESO] error while sending " + msg);
						e.printStackTrace();
					}
				}
			}
		}, delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void disconnect(String clientName) {
		clients.remove(clientName);
		System.out.println(clientName + " disconnect");
	}

}
