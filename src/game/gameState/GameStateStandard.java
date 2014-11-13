package game.gameState;

import game.LocalPlayer;
import game.OtherPlayers;
import game.Player;
import game.gameState.protocole.MsgSyncConflict;
import message.Message;
import message.MsgCard;
import message.MsgCardWithNextPlayer;
import message.MsgElection;
import message.MsgGetCriticalSection;
import message.MsgIDChoice;
import message.MsgLeader;
import message.MsgObtainCriticalSection;
import message.MsgPlayers;
import message.MsgPlaying;
import message.MsgPlayingToo;
import message.MsgPostSynch;
import message.MsgPreSynch;
import message.MsgReleaseCriticalSection;
import message.MsgResolveConflict;
import message.MsgSync;
import message.MsgTradeCards;
import reso.Reso;

/*
 *  Implements defaut receive with ignored message
 */
public abstract class GameStateStandard extends GameState {

	public GameStateStandard(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers) {
		super(reso, localPlayer, otherPlayers);
	}

	public GameStateStandard(Reso reso, LocalPlayer localPlayer, OtherPlayers otherPlayers, Player leader) {
		super(reso, localPlayer, otherPlayers, leader);
	}

	@Override
	public void receive(Message message) {
		super.ignoreMessage(message);
	}

	@Override
	public void receive(MsgCard message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgCardWithNextPlayer message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgElection message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgGetCriticalSection message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgIDChoice message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgLeader message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgObtainCriticalSection message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgPlayers message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgPlaying message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgPlayingToo message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgReleaseCriticalSection message) {
		receive((Message) message);
	}
	
	@Override
	public void receive(MsgResolveConflict message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgSync message) {
		receive((Message) message);
	}
	
	@Override
	public void receive(MsgSyncConflict message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgTradeCards message) {
		receive((Message) message);
	}

	@Override
	public void receive(MsgPreSynch message) {
		super.receive(message);
	}

	public void receive(MsgPostSynch message) {
		super.receive(message);
	}
}
