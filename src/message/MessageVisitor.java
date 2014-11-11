package message;

public interface MessageVisitor {

	void receive(Message msg);
	void receive(MsgCard msg);
	void receive(MsgCardWithNextPlayer msg);
	void receive(MsgElection msg);
	void receive(MsgGetCriticalSection msg);
	void receive(MsgIDChoice msg);
	void receive(MsgLeader msg);
	void receive(MsgObtainCriticalSection msg);
	void receive(MsgPlayers msg);
	void receive(MsgPlaying msg);
	void receive(MsgPlayingToo msg);
	void receive(MsgReleaseCriticalSection msg);
	void receive(MsgSync msg);
	void receive(MsgTradeCards msg);
	void receive(MsgPreSynch message);
	void receive(MsgPostSynch message);
}
