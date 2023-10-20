package it.game.framework.executors.interfaces;

import it.game.framework.contexts.GameContext;
import it.game.framework.stateconnections.ExceptionStateConnection;
import it.game.framework.stateconnections.GameStateConnection;
import it.game.framework.states.GameState;

import java.io.Serializable;
import java.util.List;

/**
 * ExecutorCallback is a functional class for monitoring the
 * execution of an IGameExecutor, this class contains a set of
 * method that can be overridden to monitor the various phases of
 * execution.
 */
public abstract class ExecutorCallback implements Serializable {
    /**
     * This method is executed before the loop that iters all the states
     * of the machine
     */
    public void beforeLoop(GameContext context) {
    }

    /**
     * This method is called inside the main loop just before
     * the execution of the current state.
     * @param currentState The state that has to be executed
     * @param context the GameContext of the game
     */
    public void beforeExecution(GameState currentState, GameContext context) {
    }

    /**
     * This method is called inside the main loop just after
     * the execution of the current state.
     * @param currentState The state that has been executed
     * @param context the GameContext of the game
     */
    public void afterExecution(GameState currentState, GameContext context) {
    }

    /**
     * This method is called just before the selection of the new GameState
     * @param currentState The current state
     * @param choices The list of connections from the current state
     * @param context The GameContext of the game
     */
    public void connectionChoice(GameState currentState, List<GameStateConnection> choices, GameContext context) {
    }

    /**
     * This method is called just before the selection of a new GameState in case of an exception
     * @param currentGameState The current GameState
     * @param gameStateConnections The ExceptionStateConnections that try compensate for the thrown exception
     * @param context The GameContext of the game
     */
    public void exceptionConnectionChoice(GameState currentGameState, List<ExceptionStateConnection> gameStateConnections, GameContext context) {
    }


    /**
     * This method is called just after the selection of a new GameState
     * @param currentState The current GameState
     * @param nextGameState The next GameState that will be executed in the new iteration
     * @param context The GameContext of the game
     */
    public void nextSelectedGameState(GameState currentState, GameState nextGameState, GameContext context) {
    }

    /**
     * This method is executed when the machine exits the main loop
     */
    public  void afterLoop(GameContext context) {
    }

    /**
     * This method is called when there is an error into the main loop that make the machine exit
     * @param currentState The current GameState where the exception happened
     * @param e The caught exception
     * @param context The GameContext of the game
     */
    public void caughtException(GameState currentState, Exception e, GameContext context) {
    }

}
