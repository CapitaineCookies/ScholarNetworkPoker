package game.gameState.protocole;

import game.Game;
import game.LocalPlayer;
import game.OtherPlayers;
import game.gameState.GameStateStandard;
import message.MsgElection;
import message.MsgLeader;
import reso.Reso;

public class E_ElectionGameState extends GameStateStandard {

	private boolean participant;
	private final Game game;
	
	public E_ElectionGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Game game) {
		super(reso, localPlayer, otherPlayers);
		this.participant = false;
		this.game = game;
		// TODO move participant in pre execute ?
	}

	@Override
	protected void execute() {
		sendMessageElection(localPlayer.getID());
	}
	
	@Override
	protected boolean makePrePostExecuteSynchro() {
		return true;
	}
	
	@Override
	public void receive(MsgElection message) {
		if (message.getID() > localPlayer.getID()) {
			sendMessageElection(message.getID());
		} else if (message.getID() == localPlayer.getID()) {
			log("[leader : " + localPlayer.getName() + "]");
			sendMessageLeader(localPlayer.getName(), localPlayer.getID());
			game.setLeader(localPlayer);
			notifyStepDone();
		} else if (message.getID() < localPlayer.getID() && !participant) {
			participant = true;
		}
	}
	
	@Override
	public void receive(MsgLeader message) {
		game.setLeader(getFrom(message));

		if (localPlayer.getID() != message.getLeaderID()) {
			sendMessageLeader(message.getLeaderName(), message.getLeaderID());
			log("[leader : " + message.getLeaderName() + "]");
			notifyStepDone();
		}
	}
	
	private void sendMessageLeader(String leaderName, int leaderID) {
		send(localPlayer.getNextPlayer(), new MsgLeader(leaderName, leaderID));		
	}

	public void sendMessageElection(int id) {
		send(localPlayer.getNextPlayer(), new MsgElection(id));
	}

	@Override
	public EGameState getGameState() {
		return EGameState.E_election;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.F_cardsDistribution;
	}

}
