package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;

import message.MsgElection;
import message.MsgLeader;
import message.MsgStepDone;

public class PlayerElectionGameState extends GameState {

	private boolean participant;

	public PlayerElectionGameState(Game game) {
		super(game);
		participant = false;
	}

	public void sendMessageElection(int id, String to) {
		try {
			game.sendMessage(to, new MsgElection(id, getEGameState()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void sendMessageLeader(String leaderName, int leaderID, String to) {
		try {
			game.sendMessage(to, new MsgLeader(leaderName, leaderID, getEGameState()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void receiveMessage(String from, Serializable msg) throws RemoteException {
		if (game.getCurrentGameState() instanceof CardsDistributionGameState) {
			ignoredMessage(from, msg);
		}

		if (msg instanceof MsgElection) {
			MsgElection msgElection = (MsgElection) msg;

			if (msgElection.getID() > game.getPlayer().getID()) {
				sendMessageElection(msgElection.getID(), game.getNextPlayer().getName());
			} else if (msgElection.getID() == game.getPlayer().getID()) {
				System.out.println(game.getPlayer().getName() + " leader = " + game.getPlayer().getName());
				sendMessageLeader(getPlayerName(), game.getPlayer().getID(), game.getNextPlayer().getName());
				game.setLeader(game.getPlayer());
				notifyStepDone();
			} else if (msgElection.getID() < game.getPlayer().getID() && !participant) {
				participant = true;
			}

		} else if (msg instanceof MsgLeader) {
			MsgLeader msgLeader = (MsgLeader) msg;
			game.setLeader(game.getPlayer(msgLeader.getLeaderName()));

			if (game.getPlayer().getID() != msgLeader.getLeaderID()) {
				game.setLeader(game.getPlayer(msgLeader.getLeaderName()));
				sendMessageLeader(msgLeader.getLeaderName(), msgLeader.getLeaderID(), game.getNextPlayer().getName());
				System.out.println(game.getPlayer().getName() + " leader = " + msgLeader.getLeaderName());
				notifyStepDone();
			}
		} else if (msg instanceof MsgStepDone) {
			super.receiveStepDone(from);
		} else {
			ignoredMessage(from, msg);
		}
	}

	@Override
	public void start() {

		participant = true;
		sendMessageElection(game.getPlayer().getID(), game.getNextPlayer().getName());

		waitStepDone();
		goToNextStep();
	}

	@Override
	protected void goToNextStep() {
		for (Player p : game.getOtherplayers()) {
			sendMsgStepDone(p.getName(), EGameState.election);
		}

		waitOtherPlayersDone();
		game.setCurrentGameState(EGameState.cardsDistribution);
	}

	@Override
	public EGameState getEGameState() {
		return EGameState.election;
	}
}
