package it.game.framework.statemachines.interfaces;


import it.game.framework.contexts.GameContext;
import it.game.framework.states.GameState;
import java.io.Serializable;

/**
 * Interface for the creation of lambdas that are executes
 * in during th iteration of GameState<C>s in the statemachine
 *
 * @param <C> context class that must extend GameContext
 */
public interface IterationAction<C extends GameContext> extends Serializable {

    /**
     * Interface for Lambdas creation executed in
     * GameState<C>s during the iteration of the statetree
     *
     * @param self the reference to the GameState<C> where the lambda is executed
     */
    void execute(GameState<C> self);
}