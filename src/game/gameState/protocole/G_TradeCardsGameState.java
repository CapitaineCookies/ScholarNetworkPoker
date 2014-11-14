package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateStandard;

import java.util.List;
import java.util.concurrent.Semaphore;

import message.MsgCard;
import message.MsgEndingToken;
import message.MsgGetCriticalSection;
import message.MsgObtainCriticalSection;
import message.MsgReleaseCriticalSection;
import message.MsgTradeCards;
import message.MsgTradeEnd;
import reso.Reso;
import JeuCartes.Carte;

public class G_TradeCardsGameState extends GameStateStandard {

	public static final int NB_MAX_TRADE = 2;

	private Semaphore waitCriticalSection;
	private Semaphore waitCardsReception;

	private int nbTradeMade;
	private int nbTradeMadefromlastReceiveToken;

	public G_TradeCardsGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);

		this.waitCardsReception = new Semaphore(0);
		this.waitCriticalSection = new Semaphore(0);

	}

	@Override
	protected void preExecute() {
		this.nbTradeMadefromlastReceiveToken = 1;
	}

	@Override
	protected boolean makePostPreExecuteSynchro() {
		return true;
	}

	@Override
	protected void execute() {
		nbTradeMade = 1;
		makeATrade();
	}

	@Override
	protected boolean makePrePostExecuteSynchro() {
		return true;
	}

	@Override
	public void receive(MsgEndingToken message) {
		computeMessage(message);
		sendToNextPlayer(message);
	}

	protected void sendToNextPlayer(MsgEndingToken message) {
		send(localPlayer.getNextPlayer(), message);
	}

	protected void computeMessage(MsgEndingToken message) {

		message.invalidToken(localPlayer.getName(), nbTradeMadefromlastReceiveToken);
		nbTradeMadefromlastReceiveToken = 0;
		if (nbTradeMade < NB_MAX_TRADE) {

			int nbOthersTrade = message.takeNbTradeMade(localPlayer.getName());
			log("[NbTradeMade : " + nbOthersTrade + "]");
			if (nbOthersTrade < (Math.random() * 10)) {
				makeATrade();
				nbTradeMadefromlastReceiveToken++;
				++nbTradeMade;
				message.invalidToken(localPlayer.getName(), nbTradeMadefromlastReceiveToken);
				nbTradeMadefromlastReceiveToken = 0;
			}
		}
	}

	@Override
	public void receive(MsgObtainCriticalSection message) {
		waitCriticalSection.release();
	}

	@Override
	public void receive(MsgCard message) {
		localPlayer.getHand().add(message.getCard());
		waitCardsReception.release();
	}

	@Override
	public void receive(MsgTradeEnd message) {
		notifyStepDone();
	}

	private void makeATrade() {
		log("[trade][Start]");
		getCriticalSection();
		waitCriticalSection();

		int nbCardsTrade = (int) (Math.random() * 5) + 1;
		sendTardeCards(localPlayer.getHand().getRandomCards(nbCardsTrade));

		waitCardsReception(nbCardsTrade);
		releaseCriticalSection();
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
}
