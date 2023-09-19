package it.vegas.gameframework.states.interfaces.actions;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;

import java.io.Serializable;


/**
 * Interface for the creation of lambdas that are executes
 * in the execute method of the assigned GameState
 * @param <C> context class that must extend GameContext
 */
public interface GameStateAction <C extends GameContext> extends Serializable {

    /**
     * Interface for Lambdas creation executed in
     * GameStates
     * @param self the reference to the gameState where the lambda is executed
     * @param context the reference to the context where the lambda is executed
     */
    void execute(GameState<C> self, C context);
}
