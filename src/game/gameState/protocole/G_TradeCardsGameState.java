package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateStandard;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
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

	CriticalSectionThread criticalSectionSender = null;

	private JeuCartes deck;
	private Semaphore waitCriticalSection;
	private Semaphore waitCardsReception;

	public G_TradeCardsGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);

		this.waitCardsReception = new Semaphore(0);
		this.waitCriticalSection = new Semaphore(0);

		// Leader :
		this.deck = null;
		this.criticalSectionSender = null;
	}

	@Override
	protected void preExecute() {
		if (isLeader()) {
			startTradeThread();
		}
	}

	private void startTradeThread() {
		criticalSectionSender = new CriticalSectionThread();
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
		if (isLeader()) {
			criticalSectionSender.interrupt();
		}
	}

	@Override
	public void receive(MsgTradeCards msg) {
		int nbCardsTarde = msg.getCards().size();

		if (!isLeader()) {
			throw new RuntimeException();
		}

		// TODO Suppress synchro : we are in critical section
		synchronized (deck) {
			// Add cards trade to deck
			for (Carte carte : msg.getCards()) {
				deck.ajoutCarte(carte);
			}

			// Give new cards
			for (int i = 0; i < nbCardsTarde; ++i) {
				sendCard(msg.getFrom(), deck.nvlleCarte());
			}
		}

	}

	@Override
	public void receive(MsgGetCriticalSection message) {
		if (!isLeader()) {
			log(localPlayer + " isn't the leader !");
			super.receive(message);
		}
		criticalSectionSender.add(message.getFrom());
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
		// int nbExchange = (int) (Math.random() * 3) + 1; // 0, 1, 2 or 3
		int nbExchange = 1;
		for (int i = 0; i < nbExchange; ++i) {
			log("[trade] [" + i + '/' + nbExchange + ']');
			getCriticalSection();
			waitCriticalSection();

			int nbCardsTrade = (int) (Math.random() * (5 + 1));
			sendTardeCards(localPlayer.getHand().getRandomCards(nbCardsTrade));

			waitCardsReception(nbCardsTrade);
			releaseCriticalSection();

		}
		log("[trade][Done]");
		notifyStepDone();
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
		return EGameState.G_cardsTrade;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.H_cardsShow;
	}

	class CriticalSectionThread extends Thread {

		private BlockingQueue<String> blockingQueue;
		private Semaphore criticalSectionLocker;

		public CriticalSectionThread() {
			this.blockingQueue = new LinkedBlockingDeque<>();
			this.criticalSectionLocker = new Semaphore(1);
		}

		@Override
		public void run() {
			super.run();
			log("[Thread][Start]");
			try {
				while (!interrupted()) {
					getCriticalSection();
					sendCriticalSection();
				}
			} catch (InterruptedException e) {
			} finally {
				log("[Thread][End]");
			}
		}

		private void sendCriticalSection() throws InterruptedException {
			send(blockingQueue.take(), new MsgObtainCriticalSection());
		}

		private void getCriticalSection() throws InterruptedException {
			criticalSectionLocker.acquire();
		}

		public void releaseCriticalSection() {
			criticalSectionLocker.release();
		}

		public void add(String playerName) {
			blockingQueue.add(playerName);
		}
	}
}
