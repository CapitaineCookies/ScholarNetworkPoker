package game.gameState;

import game.Game;

public class ExitGameState extends GameState {

	public ExitGameState(Game game) {
		super(game);
	}

	@Override
	public void start() {
		goToNextStep();
		System.exit(0);
	}

	@Override
	public String toString() {
		return "ExitGameState";
	}

	@Override
	protected void goToNextStep() {
		// Non next step
		// TODO se retirer du reso
	}
	

}
