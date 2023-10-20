package it.game.framework.executors;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.ExceptionLibrary;
import it.game.framework.exceptions.GameException;
import it.game.framework.executors.interfaces.ExecutorCallback;
import it.game.framework.executors.interfaces.IGameExecutor;
import it.game.framework.stateconnections.ExceptionStateConnection;
import it.game.framework.stateconnections.GameStateConnection;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * The executor class iter and activate the statemachine.
 * The executor class extends GameState so it can be used as a single GameState in a statemachine.
 * It takes the first GameState of the statemachine and execute it, then
 * It chooses the first GameStateConnection that returns true end sets it
 * as currentState and starts over until there are no more GameStates
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Component
public class GameExecutor implements IGameExecutor {

    @Value("${game.framework.executor.global_blocking_exception}")
    protected boolean GlobalExecutionExceptionBlocking;
    @Value("${game.framework.executor.game_executor_blocking_exception}")
    private boolean thisExecutionExceptionBlocking;
    protected GameState currentGameState;
    protected StateMachine stateMachine;
    protected GameContext context;
    protected List<ExecutorCallback> callbacks;

    public GameExecutor(@Value("${game.framework.executor.global_blocking_exception}") boolean globalExecutionExceptionBlocking, @Value("${game.framework.executor.game_executor_blocking_exception}") boolean thisExecutionExceptionBlocking, GameState currentGameState, StateMachine stateMachine, GameContext context, List<ExecutorCallback> callbacks) {
        GlobalExecutionExceptionBlocking = globalExecutionExceptionBlocking;
        this.thisExecutionExceptionBlocking = thisExecutionExceptionBlocking;
        this.currentGameState = currentGameState;
        this.stateMachine = stateMachine;
        this.context = context;
        this.callbacks = callbacks == null ? new ArrayList<>() : callbacks;
    }

    /**
     * This method iterates the state machine until the end of the GameStates
     * or until a GameState returns an unhandled error.
     * The execution is split in 3 stages:
     * <ul>
     *     <li>The before-loop (begin): where the the state machine is prepared to be executed by check for errors or missing GameStates</li>
     *     <li>The loop (process): where the StateMachine is explored and executed</li>
     *     <li>The after-loop (end): where the executor wrap up and finishes the execution</li>
     * </ul>
     */
    @Override
    public void execute() {
        try {
            log.info("Executing state machine");
            begin();
            callbacks.forEach(c -> c.beforeLoop(context));
            while (currentGameState != null) {
                process();
            }
            callbacks.forEach(c -> c.afterLoop(context));
        } catch (Exception e) {
            callbacks.forEach(c -> c.caughtException(currentGameState, e, context));
            log.error(GameException.format(e, currentGameState.getName()));
        }
        log.info("Ending state machine execution");
        end();
    }

    /**
     * Process contains the main loop of the execution. This one executes the action
     * inside the GameState, checks the connections and select the next GameState.
     * In case of exception check into the ExceptionGameStateConnection if it can handle and
     * continue execution therwise throw an exception and interrupt the execution of the machine.
     *
     * @throws Exception in case of unhandled exceptions throws exception
     */
    protected void process() throws Exception {
        Exception caught = null;
        try {
            log.info("Entering GameState: {}", currentGameState.getName());
            callbacks.forEach(c -> c.beforeExecution(currentGameState, context));
            currentGameState.execute(context);
            callbacks.forEach(c -> c.afterExecution(currentGameState, context));
            log.info("Exiting GameState: {}", currentGameState.getName());
        } catch (Exception e) {
            caught = e;
            callbacks.forEach(c -> c.caughtException(currentGameState, e, context));
            log.error(GameException.format(e, currentGameState.getName()));
            if (isGlobalExecutionExceptionBlocking() || isThisExecutionExceptionBlocking()) {
                throw new RuntimeException(e);
            }
        }
        GameState nextGameState;
        if (caught == null) {
            nextGameState = getNextGameState();
        } else {
            nextGameState = getNextExceptionGameState(caught);
        }
        callbacks.forEach(c -> c.nextSelectedGameState(currentGameState, nextGameState, context));
        currentGameState = nextGameState;
    }

    /**
     * Begin contains command to setup the process of the machine
     *
     * @throws GameException If he machine has some GameState missing it throws exceptions
     */
    protected void begin() throws GameException {
        executionChecks();
        if (currentGameState == null) currentGameState = stateMachine.getStartState();
    }

    /**
     * End contains the ending part of the execution of the machine
     * where everything is wrapped up and closed
     */
    protected void end() {
    }

    @Override
    public List<ExecutorCallback> getCallbacks() {
        return callbacks;
    }

    @Override
    public void setCallbacks(List<ExecutorCallback> callback) {
        this.callbacks = callback;
    }

    @Override
    public void setCallbacks(ExecutorCallback... callbacks) {
        setCallbacks(List.of(callbacks));
    }

    /**
     * Iterates all the GameStateConditions and return the game state of
     * the FIRST game state condition returning TRUE
     *
     * @return The GameState with the condition that returned true
     */
    protected GameState getNextGameState() throws Exception {
        List<GameStateConnection> GameStateConnections = stateMachine.getConnectionsOf(currentGameState);
        callbacks.forEach(c -> c.connectionChoice(currentGameState, GameStateConnections, context));
        for (GameStateConnection c : GameStateConnections) {
            if (c.checkExpression(context)) {
                return c.getResultState();
            }
        }
        return null;
    }

    /**
     * In case of an exception iterates all the ExceptionGameStateConditions and returns the game state of
     * the FIRST ExceptionGameStateCondition returning TRUE, handling the exception
     *
     * @param e The exception to check
     * @return The GameState with the condition that returned true
     */
    protected GameState getNextExceptionGameState(Exception e) throws Exception {
        List<ExceptionStateConnection> GameStateConnections = stateMachine.getExceptionConnectionsOf(currentGameState);
        callbacks.forEach(c -> c.exceptionConnectionChoice(currentGameState, GameStateConnections, context));
        for (ExceptionStateConnection c : GameStateConnections) {
            if (c.checkExpression(e)) {
                return c.getResultState();
            }
        }
        return null;
    }

    /**
     * Checks the state of the machine before the executing it
     * throwing exception in case of missing parts.
     *
     * @throws GameException In case of missing GameState
     */
    protected void executionChecks() throws GameException {
        if (stateMachine == null) {
            throw new GameException(ExceptionLibrary.get("STATEMACHINE_IS_NULL"));
        }
        if (context == null) {
            throw new GameException(ExceptionLibrary.get("CONTEXT_IS_NULL"));
        }
        if (stateMachine.getStartState() == null) {
            throw new GameException(ExceptionLibrary.get("STARTING_STATE_IS_NULL"));
        }
    }

}
