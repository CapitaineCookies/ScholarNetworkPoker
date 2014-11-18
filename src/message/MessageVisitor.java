package message;


public interface MessageVisitor {

	void receive(Message message);
	void receive(MsgCard message);
	void receive(MsgCardWithNextPlayer message);
	void receive(MsgElection message);
	void receive(MsgEndingToken message);
	void receive(MsgGetCriticalSection message);
	void receive(MsgIDChoice message);
	void receive(MsgLeader message);
	void receive(MsgObtainCriticalSection message);
	void receive(MsgPlayers message);
	void receive(MsgPlaying message);
	void receive(MsgPlayingToo message);
	void receive(MsgPostExeSyncEnd message);
	void receive(MsgPostExecutSyncRing message);
	void receive(MsgPostSync message);
	void receive(MsgPreExeSyncEnd message);
	void receive(MsgPreExecutSyncRing message);
	void receive(MsgPreSync message);
	void receive(MsgReleaseCriticalSection message);
	void receive(MsgResolveConflict message);
	void receive(MsgSync message);
	void receive(MsgSyncConflict message);
	void receive(MsgTradeCards message);
	void receive(MsgTradeEnd message);
	void receive(MsgReceiveToken message);
}
