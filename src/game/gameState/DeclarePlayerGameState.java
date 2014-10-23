package game.gameState;

import game.Game;

public class DeclarePlayerGameState extends GameState {

	public DeclarePlayerGameState(Game game) {
		super(game);
	}

	@Override
	public void start() {
		System.out.println(toString() + " start");
		game.declarePlayer();
		goToNextStep();
	}

	@Override
	protected void goToNextStep() {
		game.setGameState(new GetPlayersGameState(game));
	}

	@Override
	public String toString() {
		return "Declare Player";
	}

}
