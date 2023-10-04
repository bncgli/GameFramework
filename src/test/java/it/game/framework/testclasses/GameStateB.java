package it.game.framework.testclasses;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.states.GameState;

public class GameStateB extends GameState {
    @Override
    public void execute(GameContext c) throws GameException {
        System.out.println("Test GameStateB");
    }
}
