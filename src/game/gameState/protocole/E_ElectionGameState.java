package game.gameState.protocole;

import game.Game;
import game.LocalPlayer;
import game.OtherPlayers;
import game.gameState.GameStateRing;
import message.MsgElection;
import message.MsgLeader;
import reso.Reso;

public class E_ElectionGameState extends GameStateRing {

	private boolean participant;
	private final Game game;

	public E_ElectionGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Game game) {
		super(reso, localPlayer, otherPlayers, null);
		this.participant = false;
		this.game = game;
	}

	@Override
	protected void preExecute() {
		participant = false;
	}

	@Override
	protected boolean makePreExecuteSync() {
		return true;
	}

	@Override
	protected synchronized void execute() {
		if (!participant)
			sendMessageElection(localPlayer.getID());
		participant = true;
	}

	@Override
	protected boolean makePostExecuteSync() {
		return true;
	}

	@Override
	public synchronized void receive(MsgElection message) {
		// If exist a better ID, send the ID receive
		if (message.getID() > localPlayer.getID()) {
			sendMessageElection(message.getID());
			// If we receive own ID, we are the leader
		} else if (message.getID() == localPlayer.getID()) {
			log("[leader : " + localPlayer.getName() + "]");
			sendMessageLeader(localPlayer.getName(), localPlayer.getID());
			game.setLeader(localPlayer);
			notifyStepDone();
			// If own ID is better than the receive ID and we haven't already send our ID
		} else if (message.getID() < localPlayer.getID() && !participant) {
			sendMessageElection(localPlayer.getID());
		}
		participant = true;
	}

	@Override
	public void receive(MsgLeader message) {
		game.setLeader(getPlayer(message.getLeaderName()));

		if (localPlayer.getID() != message.getLeaderID()) {
			sendMessageLeader(message.getLeaderName(), message.getLeaderID());
			log("[leader : " + message.getLeaderName() + "]");
			notifyStepDone();
		}
	}

	private void sendMessageLeader(String leaderName, int leaderID) {
		sendToNext(new MsgLeader(leaderName, leaderID));
	}

	public void sendMessageElection(int id) {
		sendToNext(new MsgElection(id));
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
