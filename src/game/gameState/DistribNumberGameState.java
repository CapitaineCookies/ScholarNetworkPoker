package game.gameState;

import game.Game;
import game.Player;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import message.MsgIdChoice;
import message.MsgStepDone;
import message.MsgSync;

public class DistribNumberGameState extends GameState {

	int nbMsgSync;
	int nbMsgReceived;
	int nbMsgIdOk;
	boolean alreadySentOk;

	int myID;
	Map<Player, Integer> othersID;

	public DistribNumberGameState(Game game) {
		super(game);

		nbMsgSync = 0;
		nbMsgReceived = 0;
		alreadySentOk = false;
		othersID = new HashMap<>();
	}

	public void sendSynchroMessageToOthers() {
		try {
			game.sendMessageToOther(new MsgSync(getEGameState()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void sendIdMessageToOthers() {
		try {
			game.sendMessageToOther(new MsgIdChoice(myID, getEGameState()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		int taille = game.getOtherplayers().size() + 1;
		int id = (int) (Math.random() * taille);

		myID = id;

		// Send id of player
		sendIdMessageToOthers();

		waitStepDone();
		goToNextStep();
	}

	public void setNextPlayer() {
		Player maxPlayer = getMaxPlayer();
		Player nextPlayer = maxPlayer;

		if (maxPlayer == game.getPlayer()) {
			nextPlayer = getMinPlayer();
		} else {
			for (Entry<Player, Integer> currentPlayer : othersID.entrySet()) {
				if (currentPlayer.getValue() < getID(nextPlayer) && currentPlayer.getValue() > getID(game.getPlayer())) {
					nextPlayer = currentPlayer.getKey();
				}
			}
		}
		game.getPlayer().setNextPlayer(nextPlayer);
	}

	public void setPreviousPlayer() {
		Player minPlayer = getMinPlayer();
		Player previouslayer = minPlayer;

		if (minPlayer == game.getPlayer()) {
			previouslayer = getMaxPlayer();
		} else {
			for (Entry<Player, Integer> currentPlayer : othersID.entrySet()) {
				if (currentPlayer.getValue() < getID(previouslayer) && currentPlayer.getValue() > getID(game.getPlayer())) {
					previouslayer = currentPlayer.getKey();
				}
			}
		}
		game.getPlayer().setPreviousPlayer(previouslayer);
	}

	private Player getMinPlayer() {
		Player player = game.getPlayer();
		Entry<Player, Integer> minP = Collections.min(othersID.entrySet(), new CompareEntry());
		int compareMinP = minP.getValue().compareTo(getID(player));
		return (compareMinP < 0) ? minP.getKey() : player;
	}

	private Player getMaxPlayer() {
		Player player = game.getPlayer();
		Entry<Player, Integer> maxP = Collections.max(othersID.entrySet(), new CompareEntry());
		int compareMaxP = maxP.getValue().compareTo(getID(player));
		if (compareMaxP == 0)
			throw new RuntimeException();
		return (compareMaxP > 0) ? maxP.getKey() : player;
	}

	@Override
	protected void goToNextStep() {

		// Set player id
		game.getPlayer().setID(myID);

		// Set next & previous player
		setNextPlayer();
		setPreviousPlayer();

		sendMsgStepDoneToOther(getEGameState());
		waitOtherPlayersDone();
		game.setCurrentGameState(EGameState.election);

		System.out.println("goToNextStep");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + game.getPlayer() + " : " + game.getOtherplayers());
	}

	@Override
	public synchronized void receiveMessage(String from, Serializable msg) throws RemoteException {
		// !!! TODO warning danger !
		// System.out.println("yolo");
		if (game.getCurrentGameState() instanceof PlayerElectionGameState) {
			ignoredMessage(from, msg);
			return;
		}
		// System.out.println("yolo2");

		if (msg instanceof MsgIdChoice) {
			// System.out.println("in idchoice");

			MsgIdChoice msgIdChoice = (MsgIdChoice) msg;

			// System.out.println("in idchoice2" + from);
			// Set "id" to "from"
			othersID.put(game.getPlayer(from), msgIdChoice.getId());

			// System.out.println("in idchoice3");
			++nbMsgReceived;

			if (nbMsgReceived == game.getOtherplayers().size()) {
				System.out.println("[" + game.getPlayer().getName() + "]" + msg.toString() + "[Done]" + myID + " " + othersID);

				// Check the ids
				if (allDifferentID()) {
					System.out.println("all different : " + myID + " " + othersID);
					notifyStepDone();
				} else {
					if (!isDifferentID(game.getPlayer(), new ArrayList<Player>(game.getOtherplayers()))) {
						System.out.println("!isDifferent" + myID + " " + othersID);
						myID = chooseValidID();
						System.out.println("[" + game.getPlayer().getName() + "] has a new ID : " + myID + " : " + othersID);
					}
					nbMsgReceived = 0;
					sendSynchroMessageToOthers();

				}

			} else {
				System.out.println("[" + game.getPlayer().getName() + "]" + msg.toString() + " : " + nbMsgReceived + "/"
						+ game.getOtherplayers().size());
			}
		} else if (msg instanceof MsgSync) {
			nbMsgSync++;
			System.out.println(game.getPlayer().getName() + msg +  " "+  nbMsgSync + "/" + game.getOtherplayers().size());

			if (nbMsgSync == game.getOtherplayers().size()) {
				nbMsgSync = 0;
				sendIdMessageToOthers();

			}
		} else if (msg instanceof MsgStepDone) {
			super.receiveStepDone(from);
		} else {
			ignoredMessage(from, msg);
		}

	}

	private boolean allDifferentID() {
		List<Player> players = new ArrayList<>(game.getOtherplayers().size() + 1); // contient la liste de tous

		players.add(game.getPlayer());
		players.addAll(game.getOtherplayers());
		for (Player p : players)
			if (!isDifferentID(p, players))
				return false;
		return true;
	}

	public boolean isDifferentID(Player p, Collection<Player> l) {

		int idP = getID(p);
		for (Player p2 : l) {
			if (getID(p2) == idP && !p.equals(p2))
				return false;
		}

		return true;
	}

	private int getID(Player p) {
		Integer i = othersID.get(p);
		if (i != null)
			return i;
		else if (p.equals(game.getPlayer()))
			return myID;
		throw new RuntimeException("Player unexist !");
	}

	public int chooseValidID() {
		// System.out.println("test1");
		List<Player> players = new ArrayList<>(game.getOtherplayers().size() + 1); // contient la liste de tous
		// les joueurs
		List<Integer> idsNotToChoose = new ArrayList<>(); // contient la liste
															// des id à ne pas
															// System.out.println("test2");
		// choisir
		List<Integer> idsToChoose = new ArrayList<>(); // contient la liste des
														// ids à choisir
														// System.out.println("test3");
		players.add(game.getPlayer());
		players.addAll(game.getOtherplayers());

		// System.out.println("test4");
		for (int i = 0; i < game.getOtherplayers().size() + 1; ++i) {
			idsToChoose.add(i);
		}

		// System.out.println(idsToChoose);
		// System.out.println("test5");
		for (int i = 0; i < players.size(); ++i) {
			if (isDifferentID(players.get(i), players)) {
				// If is unique ID, let him is id. It's a good id
				idsNotToChoose.add(getID(players.get(i)));
			}
		}

		System.out.println(othersID);
		System.out.println(idsToChoose);
		System.out.println(idsNotToChoose);
		// System.out.println("test6");
		idsToChoose.removeAll(idsNotToChoose);
		// System.out.println("test7");
		// System.out.println(players);
		// System.out.println(idsNotToChoose);
		int index = (int) (Math.random() * idsToChoose.size());
		// System.out.println("test8");
		return idsToChoose.get(index);
	}

	@Override
	public EGameState getEGameState() {
		return EGameState.distribNumber;
	}

	class CompareEntry implements Comparator<Entry<Player, Integer>> {
		@Override
		public int compare(Entry<Player, Integer> o1, Entry<Player, Integer> o2) {
			return (o1.getValue().compareTo(o2.getValue()));
		}
	}
}
