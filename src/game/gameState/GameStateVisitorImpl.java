package game.gameState;

import java.util.logging.Logger;

import message.Message;
import message.MessageVisitor;
import message.MsgCard;
import message.MsgCardWithNextPlayer;
import message.MsgElection;
import message.MsgEndingToken;
import message.MsgGetCriticalSection;
import message.MsgIDChoice;
import message.MsgLeader;
import message.MsgObtainCriticalSection;
import message.MsgPlayers;
import message.MsgPlaying;
import message.MsgPlayingToo;
import message.MsgPostExeSyncEnd;
import message.MsgPostExecutSyncRing;
import message.MsgPostSync;
import message.MsgPreExeSyncEnd;
import message.MsgPreExecutSyncRing;
import message.MsgPreSync;
import message.MsgReceiveToken;
import message.MsgReleaseCriticalSection;
import message.MsgResolveConflict;
import message.MsgSync;
import message.MsgSyncConflict;
import message.MsgTradeCards;
import message.MsgTradeEnd;

/*
 *  Implements default receive with ignored message
 */
public abstract class GameStateVisitorImpl implements MessageVisitor {

	protected static final Logger log = Logger.getLogger("pokerClient");
	
	protected void ignoreMessage(Message message) {
		log.info(">>[Ignored] from [" + message.getFrom() + "]" + message);
	}

	@Override
	public void receive(Message message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgCard message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgCardWithNextPlayer message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgElection message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgEndingToken message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgGetCriticalSection message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgIDChoice message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgLeader message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgObtainCriticalSection message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgPlayers message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgPlaying message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgPlayingToo message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgReleaseCriticalSection message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgResolveConflict message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgSync message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgSyncConflict message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgTradeCards message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgPreSync message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgPreExecutSyncRing message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgPreExeSyncEnd message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgPostSync message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgTradeEnd message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgPostExeSyncEnd message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgPostExecutSyncRing message) {
		ignoreMessage(message);
	}

	@Override
	public void receive(MsgReceiveToken message) {
		ignoreMessage(message);		
	}
}
