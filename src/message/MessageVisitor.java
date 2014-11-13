package message;

import game.gameState.protocole.MsgSyncConflict;

public interface MessageVisitor {

	void receive(Message message);
	void receive(MsgCard message);
	void receive(MsgCardWithNextPlayer message);
	void receive(MsgElection message);
	void receive(MsgGetCriticalSection message);
	void receive(MsgIDChoice message);
	void receive(MsgLeader message);
	void receive(MsgObtainCriticalSection message);
	void receive(MsgPlayers message);
	void receive(MsgPlaying message);
	void receive(MsgPlayingToo message);
	void receive(MsgReleaseCriticalSection message);
	void receive(MsgResolveConflict message);
	void receive(MsgSync message);
	void receive(MsgTradeCards message);
	void receive(MsgPreSynch message);
	void receive(MsgPostSynch message);
	void receive(MsgSyncConflict message);
}
