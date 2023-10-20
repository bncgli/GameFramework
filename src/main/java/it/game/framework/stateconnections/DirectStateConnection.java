package it.game.framework.stateconnections;

import it.game.framework.states.GameState;

/**
 * This GameStateConnection returns always true, this is used to
 * make direct sequences of GameStates without implementing the expression everytime
 */
public class DirectStateConnection extends GameStateConnection{
    public DirectStateConnection(GameState startingState, GameState resultState) {
        super("", startingState, (c) -> true, resultState);
    }
}
