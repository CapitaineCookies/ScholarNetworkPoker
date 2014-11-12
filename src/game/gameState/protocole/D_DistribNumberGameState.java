package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateStandard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;

import message.MsgIDChoice;
import message.MsgResolveConflict;
import message.MsgSync;
import reso.Reso;
import sun.misc.Lock;

public class D_DistribNumberGameState extends GameStateStandard {

	private int myID;
	private Map<Player, Integer> othersID;

	private int nbIDChoice;
	private int nbSync;

	private int nbConflictInit;
	private int nbConflictCount;
	private MsgResolveConflict myMsgConflict;
	private Map<String, MsgResolveConflict> othersMsgConflicts;
	private boolean againsConflict;
	private boolean iHaveAgainsConflict;
	private Semaphore syncConflict;

	public D_DistribNumberGameState(Reso reso, LocalPlayer localPlayer,
			OtherPlayers otherPlayers) {
		super(reso, localPlayer, otherPlayers);
	}

	@Override
	protected void preExecute() {
		int taille = otherPlayers.size() + 1;
		myID = (int) (Math.random() * taille);
		othersID = new HashMap<>();
		nbIDChoice = 0;

		nbConflictInit = 0;
		nbConflictCount = 0;
		myMsgConflict = null;
		othersMsgConflicts = new HashMap<>();
		againsConflict = false;
		iHaveAgainsConflict = false;
		syncConflict = new Semaphore(0);

	}

	@Override
	protected boolean makePostPreExecuteSynchro() {
		return true;
	}

	@Override
	protected void execute() {
		// Send my ID to other players
		sendToOthers(new MsgIDChoice(myID));
	}

	@Override
	protected void postExecute() {
		// Set player ID
		localPlayer.setID(myID);
		setNextPlayer();
	}

	@Override
	public synchronized void receive(MsgIDChoice message) {

		// Set "id" to "from"
		othersID.put(getFrom(message), message.getID());

		// Count number of message receive
		++nbIDChoice;

		if (nbIDChoice != otherPlayers.size()) {
			log(message + " [" + nbIDChoice + "/" + otherPlayers.size() + "]");
		} else {
			log(message + "[Done]" + myID + " " + othersID);

			// Check the ids
			if (allDifferentID()) {
				log("[Check][All diff :)] " + myID + " " + othersID);
				notifyStepDone();
			} else {
				if (!iHaveDifferentID()) {
					log("[Check][Conflict :(] " + myID + " " + othersID);
					myID = chooseValidID();
					// Send messageConflict to player in conflict with me
//					for (Player player : getOtherPlayersInConflict()) {
//						myMsgConflict = new MsgResolveConflict();
//						send(player, myMsgConflict);
//					}
					log("[Check][Gene New ID] " + myID);
				} else {
					log("[Check][WaitResolve]");
				}
				nbIDChoice = 0;
				sendToOthers(new MsgSync());
			}

		}
	}

	private Lock conflictLocker = new Lock();

	@Override
	public void receive(MsgResolveConflict message) {
		if (iHaveAgainsConflict())
			iHaveAgainsConflict = true;
		else if (alreadyExist(message))
			againsConflict = true;

		synchronized (conflictLocker) {
			++nbConflictCount;
			othersMsgConflicts.put(message.getFrom(), message);

			// We have receive all conflict messages
			if (nbConflictCount == nbConflictInit) {

				for (String name : othersMsgConflicts.keySet())
					send(name, new MsgSyncConflict());
				
				try {
					syncConflict.acquire(othersMsgConflicts.size());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (againsConflict) {
					if (iHaveAgainsConflict) {
						myMsgConflict = new MsgResolveConflict();
					}
					for (String name : othersMsgConflicts.keySet())
						send(name, myMsgConflict);
				}
				else {
					setMyIDWithConflict();
				}

				nbConflictCount = 0;
			}
		}
	}

	private boolean alreadyExist(MsgResolveConflict message) {
		Set<MsgResolveConflict> set = new HashSet<>();
		for(MsgResolveConflict msgResolve : othersMsgConflicts.values())
			if(!set.add(msgResolve))
				return false;
		return true;
	}

	private boolean iHaveAgainsConflict() {
		return othersMsgConflicts.values().contains(myMsgConflict);
	}

	private void setMyIDWithConflict() {
		// TODO Auto-generated method stub
		
	}

	private List<Player> getOtherPlayersInConflict() {
		List<Player> result = new ArrayList<>();
		for (Entry<Player, Integer> playerID : othersID.entrySet())
			if (playerID.getValue().equals(myID))
				result.add(playerID.getKey());
		return result;
	}

	@Override
	public synchronized void receive(MsgSync message) {
		++nbSync;
		log(message + " " + nbSync + "/" + otherPlayers.size());

		if (nbSync == otherPlayers.size()) {
			nbSync = 0;
			sendToOthers(new MsgIDChoice(myID));
		}
	}

	private boolean allDifferentID() {
		Set<Integer> playersID = new HashSet<Integer>(otherPlayers.size() + 1); // contient
																				// lPlayere
																				// de
																				// tous
		playersID.add(myID);
		for (Integer playerID : othersID.values()) {
			if (!playersID.add(playerID)) {
				return false;
			}
		}
		return true;
	}

	private boolean iHaveDifferentID() {
		Set<Integer> ids = new HashSet<Integer>();
		ids.addAll(othersID.values());
		return ids.add(myID);
	}

	private int chooseValidID() {

		List<Integer> allChooseIDs = new ArrayList<>(otherPlayers.size() + 1);

		allChooseIDs.add(myID);
		allChooseIDs.addAll(othersID.values());

		// All possibilities
		Set<Integer> choosable = new HashSet<Integer>();
		for (int i = 0; i < otherPlayers.size() + 1; ++i) {
			choosable.add(i);
		}

		// Remove id already choose
		choosable.removeAll(allChooseIDs);

		// ReAdd duplicates
		Set<Integer> choosed = new HashSet<Integer>(otherPlayers.size() + 1);
		for (Integer id : allChooseIDs) {
			// If we can't add it, it's a duplicate number. We can choose it
			if (!choosed.add(id)) {
				choosable.add(id);
			}
		}

		System.out.println(choosable);
		System.out.println(choosed);
		int index = (int) (Math.random() * choosable.size());
		return (int) choosable.toArray()[index];
	}

	private void setNextPlayer() {
		Player maxPlayer = getMaxPlayer();
		Player nextPlayer = maxPlayer;

		if (maxPlayer == localPlayer) {
			nextPlayer = getMinPlayer();
		} else {
			for (Entry<Player, Integer> currentPlayer : othersID.entrySet()) {
				if (currentPlayer.getValue() < getID(nextPlayer)
						&& currentPlayer.getValue() > getID(localPlayer)) {
					nextPlayer = currentPlayer.getKey();
				}
			}
		}
		localPlayer.setNextPlayer(nextPlayer);
	}

	private Player getMinPlayer() {
		Player player = localPlayer;
		Entry<Player, Integer> minP = Collections.min(othersID.entrySet(),
				new CompareEntry());
		int compareMinP = minP.getValue().compareTo(getID(player));
		return (compareMinP < 0) ? minP.getKey() : player;
	}

	private Player getMaxPlayer() {
		Player player = localPlayer;
		Entry<Player, Integer> maxP = Collections.max(othersID.entrySet(),
				new CompareEntry());
		int compareMaxP = maxP.getValue().compareTo(getID(player));
		if (compareMaxP == 0) {
			throw new RuntimeException();
		}
		return (compareMaxP > 0) ? maxP.getKey() : player;
	}

	private int getID(Player p) {
		Integer i = othersID.get(p);
		if (i != null) {
			return i;
		} else if (p.equals(localPlayer)) {
			return myID;
		}
		throw new RuntimeException("Player unexist !");
	}

	class CompareEntry implements Comparator<Entry<Player, Integer>> {

		@Override
		public int compare(Entry<Player, Integer> o1, Entry<Player, Integer> o2) {
			return (o1.getValue().compareTo(o2.getValue()));
		}
	}

	@Override
	public EGameState getGameState() {
		return EGameState.D_distribNumber;
	}

	@Override
	public EGameState getNextState() {
		return EGameState.E_election;
	}

}