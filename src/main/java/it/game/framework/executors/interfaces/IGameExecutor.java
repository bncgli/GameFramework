package it.game.framework.executors.interfaces;

import it.game.framework.contexts.GameContext;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;

import java.util.Collection;
import java.util.List;

/**
 * IGameExecutor is an interface that represents the machine's executors.
 * This interface is been created to manage to grant interchangeability
 * between executors and to grant the possibility to implement a custom
 * GameExecutor.
 */
public interface IGameExecutor {

    /**
     * The main method of each Executor,
     * this method iters and executes the machine.
     */
    void execute();

    /**
     * Returns the current GameState
     * @return The current GameState
     */
    GameState getCurrentGameState();

    /**
     * Returns the StateMachine
     * @return The StateMachine
     */
    StateMachine getStateMachine();

    /**
     * Returns the GameContext
     * @return The GameContext
     */
    GameContext getContext();

    /**
     * Sets the current GameState
     * @param currentGameState The current GameState
     */
    void setCurrentGameState(GameState currentGameState);

    /**
     * Sets the StateMachine
     * @param stateMachine The StateMachine
     */
    void setStateMachine(StateMachine stateMachine);

    /**
     * Sets the GameContext of the game
     * @param context The GameContext of the game
     */
    void setContext(GameContext context);

    /**
     * Returns the callbacks
     * @return The list of callbacks
     */
    List<ExecutorCallback> getCallbacks();

    /**
     * Set the list of callbacks
     * @param callbacks The list of callbacks
     */
    void setCallbacks(List<ExecutorCallback> callbacks);

    /**
     * Set the list of callbacks
     * @param callbacks The list of callbacks
     */
    void setCallbacks(ExecutorCallback... callbacks);
}
