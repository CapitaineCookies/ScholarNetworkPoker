package game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OtherPlayers {

	private Map<String, RemotePlayer> players;

	public OtherPlayers() {
		players = new HashMap<String, RemotePlayer>();
	}
	
	public Map<String, RemotePlayer> getMap() {
		return players;
	}
	
	public Collection<RemotePlayer> getPlayers() {
		return players.values();
	}
	
	public boolean contains(Player player) {
		return players.containsValue(player);
	}
	
	public boolean contains(String playerName) {
		return players.containsKey(playerName);
	}
	
	public Player getPlayer(String playerName) {
		return players.get(playerName);
	}

	public void addAll(Collection<RemotePlayer> otherPlayers) {
		for(RemotePlayer player : otherPlayers)
			players.put(player.getName(), player);
	}
	
	public int size() {
		return players.size();
	}

	public boolean remove(Player player) {
		return remove(player.getName());
	}

	public boolean remove(String player) {
		return players.remove(player) != null;
	}

}
