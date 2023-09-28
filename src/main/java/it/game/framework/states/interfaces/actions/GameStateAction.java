package it.game.framework.states.interfaces.actions;

import it.game.framework.contexts.GameContext;
import it.game.framework.states.GameState;

import java.io.Serializable;


/**
 * Interface for the creation of lambdas that are executes
 * in the execute method of the assigned GameState
 */
public interface GameStateAction extends Serializable {

    /**
     * Interface for Lambdas creation executed in
     * GameState<C>s
     * @param self the reference to the GameState<C> where the lambda is executed
     * @param context the reference to the context where the lambda is executed
     */
    <C extends GameContext> void execute(GameState<C> self, C context);
}
