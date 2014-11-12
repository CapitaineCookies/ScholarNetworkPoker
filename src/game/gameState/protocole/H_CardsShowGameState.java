package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateStandard;
import message.MsgCardWithNextPlayer;
import reso.Reso;
import JeuCartes.Hand;

public class H_CardsShowGameState extends GameStateStandard {
	
	public H_CardsShowGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);
	}

	private int countCard;

	@Override
	protected void preExecute() {
		countCard = 0;
	}
	
	@Override
	protected boolean makePostPreExecuteSynchro() {
		// TODO not useful, remove the postSync of previous state
		return true;
	}
	
	@Override
	protected void execute() {
		if(isLeader())
			sendNextShowCard();
			
	}
	
	@Override
	public synchronized void receive(MsgCardWithNextPlayer message) {
		if(localPlayer.equals(message.getNextPlayer()))
			sendNextShowCard();
	}

	private void sendNextShowCard() {
		countCard ++;
                broadcast(new MsgCardWithNextPlayer(localPlayer.getHand().pollRandomCard(), localPlayer.getNextPlayer().getName()));
                if(countCard == Hand.nbCardPerPlayer)
                    notifyStepDone();
	}

	@Override
	public EGameState getGameState() {
		return EGameState.H_cardsShow;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.Z_exit;
	}


}
