package it.game.framework.stateconnections;

import it.game.framework.states.GameState;

public class DirectStateConnection extends GameStateConnection{
    public DirectStateConnection(GameState startingState, GameState resultState) {
        super("", startingState, (c) -> true, resultState);
    }
}
