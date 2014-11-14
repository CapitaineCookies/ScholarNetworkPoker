package game.gameState.protocole;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import message.MsgCard;
import message.MsgEndingToken;
import message.MsgGetCriticalSection;
import message.MsgObtainCriticalSection;
import message.MsgReleaseCriticalSection;
import message.MsgTradeCards;
import message.MsgTradeEnd;
import reso.Reso;
import JeuCartes.Carte;
import JeuCartes.JeuCartes;

public class GL_TradeCardsGameState extends G_TradeCardsGameState {

	private JeuCartes deck;
	CriticalSectionThread criticalSectionSender = null;

	public GL_TradeCardsGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader, JeuCartes deck) {
		super(reso, localPlayer, otherPlayers, leader);
		this.deck = deck;
		this.criticalSectionSender = new CriticalSectionThread();
	}

	@Override
	protected void preExecute() {
		super.preExecute();
		this.criticalSectionSender.start();
		send(localPlayer.getName(), new MsgEndingToken());
	}
	
	@Override
	public void receive(MsgEndingToken message) {
		if(message.isValid())
			broadcast(new MsgTradeEnd());
		else {
			message.initialiseValid();
			computeMessage(message);
			sendToNextPlayer(message);
		}
	}

	@Override
	protected void postExecute() {
		if (isLeader()) {
			criticalSectionSender.interrupt();
		}
	}

	@Override
	public void receive(MsgGetCriticalSection message) {
		criticalSectionSender.add(message.getFrom());
	}

	@Override
	public void receive(MsgReleaseCriticalSection message) {
		criticalSectionSender.releaseCriticalSection();
	}

	@Override
	public void receive(MsgTradeCards msg) {
		int nbCardsTarde = msg.getCards().size();

		// synchronized not used : we are in critical section
		
		// Add cards trade to deck
		for (Carte carte : msg.getCards()) {
			deck.ajoutCarte(carte);
		}

		// Give new cards
		for (int i = 0; i < nbCardsTarde; ++i) {
			sendCard(msg.getFrom(), deck.nvlleCarte());
		}
	}

	private void sendCard(String to, Carte card) {
		send(to, new MsgCard(card));
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
