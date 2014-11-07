package game.gameState;

import game.Game;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import message.MsgCard;
import message.MsgGetCriticalSection;
import message.MsgObtainCriticalSection;
import message.MsgReleaseCriticalSection;
import message.MsgStepDone;
import message.MsgTradeCards;
import JeuCartes.Carte;
import JeuCartes.JeuCartes;

public class CardsTradeGameState extends GameState {

	private Semaphore waitCriticalSection;
	private Semaphore waitCardsReception;

	// Leader :
	private JeuCartes deck;
	private Queue<String> waitingList;
	private CriticalSectionSenderThread criticalSectionSender;

	public CardsTradeGameState(Game game) {
		super(game);
		this.waitCardsReception = new Semaphore(0);
		this.waitCriticalSection = new Semaphore(0);

		// Leader :
		this.deck = null;
		this.criticalSectionSender = null;
		this.waitingList = new LinkedList<>();
	}

	// Leader
	public void setDeck(JeuCartes deck) {
		this.deck = deck;
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		// Leader :
		if (msg instanceof MsgTradeCards)
			receiveTradeCards(from, (MsgTradeCards) msg);
		else if (msg instanceof MsgGetCriticalSection)
			receiveGetCriticalSection(from);
		else if (msg instanceof MsgReleaseCriticalSection)
			receiveReleaseCriticalSection(from);

		// Common :
		else if (msg instanceof MsgObtainCriticalSection)
			receiveObtainCriticalSection(from);
		else if (msg instanceof MsgCard)
			receiveCard((MsgCard) msg);
		else if (msg instanceof MsgStepDone)
			receiveStepDone(from);
		else
			ignoredMessage(from, msg);
	}

	private void receiveObtainCriticalSection(String from) {
		waitCriticalSection.release();
	}

	private void receiveCard(MsgCard msgCard) {
		game.getPlayer().getHand().add(msgCard.getCard());
		waitCardsReception.release();
	}

	@Override
	public void start() {
		if (game.isLeader()) {
			System.out.println("[" + getPlayerName() + "][" + getEGameState() + "][start] thread");
			startTradeThread();
		}

		tradeHisCards();

		// waitStepDone();
		sendMsgStepDoneToOther(getEGameState());
		waitOtherPlayersDone();
		goToNextStep();
	}

	private void startTradeThread() {
		criticalSectionSender = new CriticalSectionSenderThread(game, waitingList);
		criticalSectionSender.start();
	}

	private void tradeHisCards() {

		int nbExchange = (int) (Math.random() * 3) + 1; // 0, 1, 2 or 3
		for (int i = 0; i < nbExchange; ++i) {
			System.out.println("[" + getPlayerName() + "][" + getEGameState() + "] trade [" + i + '/' + nbExchange
					+ ']');
			getCriticalSection();
			System.out.println("get criticalSection");
			waitCriticalSection();
			System.out.println("wait criticalSection");

			int nbCardsTrade = (int) Math.random() * 5 + 1;
			sendTardeCards(game.getPlayer().getHand().getRandomCards(nbCardsTrade));

			System.out.println("send cardReception");
			waitCardsReception(nbCardsTrade);

			System.out.println("wait cardReception");
			releaseCriticalSection();
			System.out.println("release criticalSection");

		}
		System.out.println("[" + getPlayerName() + "][" + getEGameState() + "] trade [Done]");

		// TODO notify step done ?
	}

	private void releaseCriticalSection() {
		try {
			game.sendMessage(game.getLeader(), new MsgReleaseCriticalSection(getEGameState()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void waitCriticalSection() {
		try {
			// Is release by receive MsgObtainCriticalSection()
			waitCriticalSection.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void getCriticalSection() {
		try {
			// Send message to receive an MsgObtainCriticalSection() in response
			game.sendMessage(game.getLeader(), new MsgGetCriticalSection(getEGameState()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void waitCardsReception(int nbCardsTrade) {
		try {
			waitCardsReception.acquire(nbCardsTrade);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendTardeCards(List<Carte> cards) {
		try {
			game.sendMessage(game.getLeader(), new MsgTradeCards(cards, getEGameState()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void goToNextStep() {
		System.out.println("New Cards : " + game.getPlayer().getHand());
		if (criticalSectionSender != null)
			criticalSectionSender.stop();
		game.setCurrentGameState(EGameState.cardsShow);

	}

	@Override
	public EGameState getEGameState() {
		return EGameState.cardsTrade;
	}

	// *******************************
	// Leader
	// *******************************

	private synchronized void receiveGetCriticalSection(String from) {
		if (!game.isLeader())
			throw new RuntimeException(game.getPlayer() + " isn't leader !");


		synchronized (waitingList) {
			waitingList.add(from);
			waitingList.notify();
			// TODO notify a thread in the same synchronized object than the notify
		}
	}

	private void receiveReleaseCriticalSection(String from) {
		criticalSectionSender.releaseCriticalSection();
	}

	private void receiveTradeCards(String from, MsgTradeCards msg) {
		int nbCardsTarde = msg.getCards().size();

		if (!game.isLeader())
			throw new RuntimeException();

		// TODO Suppress synchro : we are in critical section
		synchronized (deck) {
			// Add cards trade to deck
			for (Carte carte : msg.getCards()) {
				deck.ajoutCarte(carte);
			}

			// Give new cards
			for (int i = 0; i < nbCardsTarde; ++i)
				sendCard(from, deck.nvlleCarte());
		}

	}

	// Same in cardsDistrib
	private void sendCard(String player, Carte card) {
		try {
			game.sendMessage(player, new MsgCard(card, getEGameState()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	class CriticalSectionSenderThread extends Thread {

		private Game gameThread;
		private Queue<String> queueThread;
		private Semaphore criticalSectionLocker;

		public CriticalSectionSenderThread(Game game, Queue<String> queue) {
			this.gameThread = game;
			this.queueThread = queue;
			this.criticalSectionLocker = new Semaphore(1);
		}

		public void releaseCriticalSection() {
			criticalSectionLocker.release();
		}

		@Override
		public void run() {
			super.run();
			while (true) {
				try {
					waitQueueEvent();
				} catch (InterruptedException e) {
					break;
				}
				getCriticalSection();
				sendCriticalSection();
			}
			System.out.println("[" + getPlayerName() + "][" + getEGameState() + "][end] thread");
		}

		private void sendCriticalSection() {
			String player;
			synchronized (queueThread) {
				player = queueThread.poll();
			}
			try {
				gameThread.sendMessage(player, new MsgObtainCriticalSection(getEGameState()));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		private void getCriticalSection() {
			try {
				criticalSectionLocker.acquire();
			} catch (InterruptedException e) {
			}
		}

		private void waitQueueEvent() throws InterruptedException {
			synchronized (queueThread) {
				if (queueThread.isEmpty()) {
					// Wait
					queueThread.wait();
				}
			}
		}
	}
}
