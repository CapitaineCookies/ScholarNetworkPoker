package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateStandard;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import message.MsgCard;
import message.MsgGetCriticalSection;
import message.MsgObtainCriticalSection;
import message.MsgReleaseCriticalSection;
import message.MsgTradeCards;
import reso.Reso;
import JeuCartes.Carte;
import JeuCartes.JeuCartes;

public class G_TradeCardsGameState extends GameStateStandard {

	CriticalSectionSenderThread criticalSectionSender = null;
	// TODO implement a complexness wave of sender : executor or BlockingQueue

	private JeuCartes deck;
	private Queue<String> waitingList;
	private Semaphore waitCriticalSection;
	private Semaphore waitCardsReception;

	public G_TradeCardsGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);

		this.waitCardsReception = new Semaphore(0);
		this.waitCriticalSection = new Semaphore(0);

		// Leader :
		this.deck = null;
		this.criticalSectionSender = null;
		this.waitingList = new LinkedList<>();
	}

	@Override
	protected void preExecute() {
		if (isLeader()) {
			startTradeThread();
		}
	}

	private void startTradeThread() {
		criticalSectionSender = new CriticalSectionSenderThread(waitingList);
		criticalSectionSender.start();

	}

	// Leader
	public void setDeck(JeuCartes deck) {
		this.deck = deck;
	}

	@Override
	protected boolean makePostPreExecuteSynchro() {
		return true;
	}

	@Override
	protected void execute() {
		tradeHisCards();
	}

	@Override
	protected boolean makePrePostExecuteSynchro() {
		return true;
	}

	@Override
	protected void postExecute() {
		if (isLeader())
			criticalSectionSender.interrupt();
	}

	@Override
	public void receive(MsgTradeCards msg) {
		int nbCardsTarde = msg.getCards().size();

		if (!isLeader())
			throw new RuntimeException();

		// TODO Suppress synchro : we are in critical section
		synchronized (deck) {
			// Add cards trade to deck
			for (Carte carte : msg.getCards()) {
				deck.ajoutCarte(carte);
			}

			// Give new cards
			for (int i = 0; i < nbCardsTarde; ++i)
				sendCard(msg.getFrom(), deck.nvlleCarte());
		}

	}

	@Override
	public synchronized void receive(MsgGetCriticalSection message) {
		if (!isLeader())
			throw new RuntimeException(localPlayer + " isn't the leader !");

		synchronized (waitingList) {
			waitingList.add(message.getFrom());
			waitingList.notify();
			// TODO notify a thread in the same synchronized object than the notify
		}
	}

	@Override
	public void receive(MsgReleaseCriticalSection message) {
		criticalSectionSender.releaseCriticalSection();
	}

	@Override
	public void receive(MsgObtainCriticalSection message) {
		waitCriticalSection.release();
	}

	@Override
	public void receive(MsgCard msgCard) {
		localPlayer.getHand().add(msgCard.getCard());
		waitCardsReception.release();
	}

	private void tradeHisCards() {
		int nbExchange = (int) (Math.random() * 3) + 1; // 0, 1, 2 or 3
		for (int i = 0; i < nbExchange; ++i) {
			log("[trade] [" + i + '/' + nbExchange + ']');
			getCriticalSection();
			waitCriticalSection();

			int nbCardsTrade = (int) Math.random() * 5 + 1;
			sendTardeCards(localPlayer.getHand().getRandomCards(nbCardsTrade));

			waitCardsReception(nbCardsTrade);
			releaseCriticalSection();

		}
		log("[trade][Done]");

	}

	private void getCriticalSection() {
		send(leader, new MsgGetCriticalSection());
	}

	private void waitCriticalSection() {
		try {
			// Is release by receive MsgObtainCriticalSection()
			waitCriticalSection.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void releaseCriticalSection() {
		send(leader, new MsgReleaseCriticalSection());
	}

	private void waitCardsReception(int nbCardsTrade) {
		try {
			waitCardsReception.acquire(nbCardsTrade);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendCard(String to, Carte card) {
		send(to, new MsgCard(card));
	}

	private void sendTardeCards(List<Carte> cards) {
		send(leader, new MsgTradeCards(cards));
	}

	@Override
	public EGameState getGameState() {
		return EGameState.E_election;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.G_cardsTrade;
	}

	class CriticalSectionSenderThread extends Thread {

		private Queue<String> queueThread;
		private Semaphore criticalSectionLocker;

		// TODO Use a BlockingQueue

		public CriticalSectionSenderThread(Queue<String> queue) {
			this.queueThread = queue;
			this.criticalSectionLocker = new Semaphore(1);
		}

		public void releaseCriticalSection() {
			criticalSectionLocker.release();
		}

		@Override
		public void run() {
			super.run();
			log("[Thread][Start]");
			while (!interrupted()) {
				try {
					waitQueueEvent();
					getCriticalSection();
					sendCriticalSection();
				} catch (InterruptedException e) {
					break;
				}
			}
			log("[Thread][End]");
		}

		private void sendCriticalSection() {
			String player;
			synchronized (queueThread) {
				player = queueThread.poll();
			}
			send(player, new MsgObtainCriticalSection());
		}

		private void getCriticalSection() {
			try {
				criticalSectionLocker.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
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
