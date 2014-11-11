//package obsolete;
//
//import game.Game;
//import game.Player;
//
//import java.io.Serializable;
//import java.rmi.RemoteException;
//import java.util.List;
//import java.util.Vector;
//
//import message.MsgStepDone;
//
//public abstract class GameState {
//
//	public enum EGameState {
//
//		getReso, declaration, getOthers, distribNumber, election, cardsDistribution, cardsShow, cardsTrade, exit;
//		
//		@Override
//		public String toString() {
//			char firstChar = super.toString().charAt(0);
//			return Character.toUpperCase(firstChar) + super.toString().substring(1);
//		}
//	}
//	
//	protected int nbMsgSyncState;
//	protected Object stepDone;
//	protected Object stepOtherPlayersDone;
//	protected Game game;
//	protected List<Player> playersReady;
//
//	private int nbMessageStepDone;
//
//	public GameState(Game game) {
//		this.stepDone = new Object();
//		this.game = game;
//		this.playersReady = new Vector<>();
//		nbMessageStepDone = 0;
//	}
//
//	public abstract void receiveMessage(String from, Serializable msg) throws RemoteException;
//
//	protected void ignoredMessage(String from, Serializable msg) {
//		System.out.println("Message from " + from + " ignored");
//	}
//
//	protected String getPlayerName() {
//		return game.getPlayer().getName();
//	}
//
//	protected void sendMsgStepDoneToOther(EGameState gameState) {
//		try {
//			game.sendMessageToOther(new MsgStepDone(gameState));			
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
//
//	protected void sendMsgStepDone(String to, EGameState gameState) {
//		String playerName = game.getPlayer().getName();
//		try {
//			game.sendMessage(playerName, to, new MsgStepDone(gameState));
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
//
//	protected void notifyStepDone() {
//		synchronized (stepDone) {
//			stepDone.notify();
//
//		}
//	}
//
//	protected void notifyOtherPlayersDone() {
//		synchronized (stepOtherPlayersDone) {
//			stepOtherPlayersDone.notify();
//
//		}
//	}
//
//	protected void waitStepDone() {
//		synchronized (stepDone) {
//			try {
//				stepDone.wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	protected synchronized void receiveStepDone(String from) {
//		nbMessageStepDone++;
//		System.out.println("nbMessageStepDone=" + nbMessageStepDone + "/" + game.getOtherplayers().size());
//		if (nbMessageStepDone == game.getOtherplayers().size()) {
//			notifyOtherPlayersDone();
//			System.out.println("Is notify");
//		}
//
//	}
//
//	protected void waitOtherPlayersDone() {
//		while (nbMessageStepDone < game.getOtherplayers().size()) {
//			@SuppressWarnings("unused")
//			int i = 0;
//		}
//
//		// if(nbMessageStepDone < game.getOtherplayer().size()) {
//		// synchronized (stepOtherPlayersDone) {
//		// try {
//		// stepOtherPlayersDone.wait();
//		// } catch (InterruptedException e) {
//		// e.printStackTrace();
//		// }
//		// }
//		// }
//	}
//
//	public abstract void start();
//
//	protected abstract void goToNextStep();
//
//	public String toString() {
//		String name = getClass().getSimpleName().replace("GameState", "");
//		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
//	}
//
//	public abstract EGameState getEGameState();
//}
