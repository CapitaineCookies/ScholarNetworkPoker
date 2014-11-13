package game.gameState.protocole;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.GameStateStandard;

import java.util.ArrayList;
import java.util.Collection;
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
import reso.Reso;

public class D_DistribNumberGameState extends GameStateStandard {

	private int myID;
	private Map<Player, Integer> othersID;

	private int nbIDChoice;

	private int nbConflictCount;
	private Set<Player> playersInConflictWithMe;
	private MsgResolveConflict myMsgConflict;
	private Map<String, MsgResolveConflict> othersMsgConflicts;
	private Semaphore syncConflict;
	private Semaphore waitAllConflictResolve;

	public D_DistribNumberGameState(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers) {
		super(reso, localPlayer, otherPlayers);
	}

	@Override
	protected void preExecute() {
		int taille = otherPlayers.size() + 1;
		myID = (int) (Math.random() * taille);
		othersID = new HashMap<>();

		nbIDChoice = 0;

		nbConflictCount = 0;
		myMsgConflict = null;
		playersInConflictWithMe = new HashSet<>();
		othersMsgConflicts = new HashMap<>();
		waitAllConflictResolve = new Semaphore(0);
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
	protected boolean makePrePostExecuteSynchro() {
		return true;
	}

	@Override
	protected void postExecute() {
		// Set player ID
		localPlayer.setID(myID);
		setNextPlayer();
	}

	@Override
	public void receive(MsgIDChoice message) {
		try {

			// Set "id" to "from"
			othersID.put(getFrom(message), message.getID());

			// Count number of message receive
			++nbIDChoice;

			if (nbIDChoice < otherPlayers.size()) {
				log(message + " [" + nbIDChoice + "/" + otherPlayers.size() + "]");
			} else if (nbIDChoice == otherPlayers.size()) {

				log(message + "[Done]" + myID + " " + othersID);

				if (!iHaveDifferentID()) {

					log("[Check][Conflict :(] " + myID + " " + othersID);
					playersInConflictWithMe = getOtherPlayersInConflict();
					myMsgConflict = new MsgResolveConflict();

					sendMsgSyncConflict(playersInConflictWithMe);
					waitMsgSyncConflict(playersInConflictWithMe.size());
					// myID = chooseValidID();
					// Send messageConflict to player in conflict with me
					log("[NbConflict][Size : " + playersInConflictWithMe.size() + "]");
					for (Player player : playersInConflictWithMe) {
						send(player, myMsgConflict);
					}
					waitAllConflictResolve.acquire(getNumberOfConflict(othersID.values()));
				} else {
					log("[Check][WaitResolve] I've different ID. Nb of conflicts : " + getNumberOfConflict(othersID.values()));
					waitAllConflictResolve.acquire(getNumberOfConflict(othersID.values()));
				}

				notifyStepDone();

			} else {
				waitAllConflictResolve.release();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void receive(MsgSyncConflict message) {
		syncConflict.release();
		log(syncConflict.toString());
	}

	private Object conflictLocker = new Object();

	@Override
	public void receive(MsgResolveConflict message) {
		synchronized (conflictLocker) {

			// Check conflict in weight
			++nbConflictCount;
			othersMsgConflicts.put(message.getFrom(), message);

			// We have receive all conflict messages
			if (nbConflictCount == playersInConflictWithMe.size()) {
				nbConflictCount = 0;

				sendMsgSyncConflict(playersInConflictWithMe);
				waitMsgSyncConflict(playersInConflictWithMe.size());

				if (againsConflict()) {
					log("[DetectConflict]");
					myMsgConflict = new MsgResolveConflict();
					for (Player name : playersInConflictWithMe)
						send(name, myMsgConflict);
				} else {
					setMyIDWithConflict();
					waitAllConflictResolve.release();
					log("[ConflcitResolve][NewID : " + myID + "]");
					sendToOthers(new MsgIDChoice(myID));
				}

			}
		}
	}

	private void sendMsgSyncConflict(Collection<Player> players) {
		log("[Sync] send to " + playersInConflictWithMe.size() + " players");
		for (Player player : players)
			send(player, new MsgSyncConflict());
	}

	private void waitMsgSyncConflict(int size) {
		try {
			log("[Sync] nb message expected " + playersInConflictWithMe.size());
			syncConflict.acquire(size);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean againsConflict() {
		Set<Double> set = new HashSet<>();
		for (MsgResolveConflict msgResolve : othersMsgConflicts.values())
			if (!set.add(msgResolve.getWeight()))
				return true;
		return !set.add(myMsgConflict.getWeight());
	}

	private void setMyIDWithConflict() {
		log("[Check][StartSetNewID] ");
		List<Integer> availableID = new ArrayList<Integer>();
		//
		int numConflict = getNumberOfConflictBefore(othersID.values(), myID);

		log("[Check][StartSetNewID] 2");

		// Add all non selected value + the id in conflict for me
		for (int i = 0; i < otherPlayers.size() + 1; ++i) {
			if (!othersID.values().contains(i)) {
				availableID.add(i);
				System.out.println(i + " : " + !othersID.values().contains(i));
			}
		}
		availableID.add(myID);

		log("[Check][StartSetNewID] 3");
		System.out.println(availableID);
		System.out.println(othersID);
		System.out.println(numConflict);
		System.out.println(getPlaceInConflict());

		myID = availableID.get(numConflict + getPlaceInConflict());
		log("[Check][Gene New ID] " + myID);
	}

	private int getNumberOfConflict(Collection<Integer> collection) {
		return getNumberOfConflictBefore(collection, -1);
	}

	private int getNumberOfConflictBefore(Collection<Integer> usedID, int limit) {
		int numberOfConflict = 0;

		Set<Integer> uniqueSet = new HashSet<>(usedID);
		for (Integer temp : uniqueSet) {
			if (temp >= limit && limit >= 0)
				break;
			int freqTemp = Collections.frequency(usedID, temp);
			if (freqTemp > 1)
				numberOfConflict += freqTemp;
		}
		return numberOfConflict;
	}

	private int getPlaceInConflict() {
		int place = 0;
		for (MsgResolveConflict msg : othersMsgConflicts.values()) {
			if (msg.getWeight() < myMsgConflict.getWeight())
				place++;
		}
		return place;
	}

	private Set<Player> getOtherPlayersInConflict() {
		Set<Player> result = new HashSet<>();
		for (Entry<Player, Integer> playerID : othersID.entrySet())
			if (playerID.getValue().equals(myID))
				result.add(playerID.getKey());
		return result;
	}

	private boolean iHaveDifferentID() {
		Set<Integer> ids = new HashSet<Integer>();
		ids.addAll(othersID.values());
		return ids.add(myID);
	}

	private void setNextPlayer() {
		Player maxPlayer = getMaxPlayer();
		Player nextPlayer = maxPlayer;

		if (maxPlayer == localPlayer) {
			nextPlayer = getMinPlayer();
		} else {
			for (Entry<Player, Integer> currentPlayer : othersID.entrySet()) {
				if (currentPlayer.getValue() < getID(nextPlayer) && currentPlayer.getValue() > getID(localPlayer)) {
					nextPlayer = currentPlayer.getKey();
				}
			}
		}
		localPlayer.setNextPlayer(nextPlayer);
	}

	private Player getMinPlayer() {
		Player player = localPlayer;
		Entry<Player, Integer> minP = Collections.min(othersID.entrySet(), new CompareEntry());
		int compareMinP = minP.getValue().compareTo(getID(player));
		return (compareMinP < 0) ? minP.getKey() : player;
	}

	private Player getMaxPlayer() {
		Player player = localPlayer;
		Entry<Player, Integer> maxP = Collections.max(othersID.entrySet(), new CompareEntry());
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
