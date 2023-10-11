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

import java.util.List;
import java.util.Optional;


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
    protected Optional<ExecutorCallback> callback;

    public GameExecutor(@Value("${game.framework.executor.global_blocking_exception}") boolean globalExecutionExceptionBlocking, @Value("${game.framework.executor.game_executor_blocking_exception}") boolean thisExecutionExceptionBlocking, GameState currentGameState, StateMachine stateMachine, GameContext context, ExecutorCallback callback) {
        GlobalExecutionExceptionBlocking = globalExecutionExceptionBlocking;
        this.thisExecutionExceptionBlocking = thisExecutionExceptionBlocking;
        this.currentGameState = currentGameState;
        this.stateMachine = stateMachine;
        this.context = context;
        this.callback = Optional.ofNullable(callback);
    }

    /**
     * Execute overrides the execute method.
     * This method iters the state machine until the end of the GameStates
     * or until a GameState returns an error. Each step is sended to
     * the monitor interface.
     */
    @Override
    public void execute() {
        try {
            log.info("Executing state machine");
            begin();
            callback.ifPresent(c -> c.beforeLoop(context));
            while (currentGameState != null) {
                process();
            }
            callback.ifPresent(c -> c.afterLoop(context));
        } catch (Exception e) {
            callback.ifPresent(c -> c.caughtException(currentGameState, e, context));
            log.error(GameException.format(e, currentGameState.getName()));
        }
        log.info("Ending state machine execution");
        end();
    }

    protected void process() throws Exception {
        Exception caught = null;
        try {
            log.info("Entering GameState: {}", currentGameState.getName());
            callback.ifPresent(c -> c.beforeExecution(currentGameState, context));
            currentGameState.execute(context);
            callback.ifPresent(c -> c.afterExecution(currentGameState, context));
            log.info("Exiting GameState: {}", currentGameState.getName());
        } catch (Exception e) {
            caught = e;
            callback.ifPresent(c -> c.caughtException(currentGameState, e, context));
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
        callback.ifPresent(c -> c.nextSelectedGameState(currentGameState, nextGameState, context));
        currentGameState = nextGameState;
    }

    protected void begin() throws GameException {
        executionChecks();
        if (currentGameState == null) currentGameState = stateMachine.getStartState();
    }

    protected void end() {

    }

    @Override
    public ExecutorCallback getCallback() {
        return callback.orElse(null);
    }

    @Override
    public void setCallback(ExecutorCallback callback) {
        this.callback = Optional.ofNullable(callback);
    }

    /**
     * Iters all the GameStateconditions and return the game state of
     * the FIRST game state condition returning TRUE
     *
     * @return The GameState with the condition that returned true
     */
    protected GameState getNextGameState() throws Exception {
        List<GameStateConnection> GameStateConnections = stateMachine.getConnectionsOf(currentGameState);
        callback.ifPresent(c -> c.connectionChoice(currentGameState, GameStateConnections, context));
        for (GameStateConnection c : GameStateConnections) {
            if (c.checkExpression(context)) {
                return c.getResultState();
            }
        }
        return null;
    }

    protected GameState getNextExceptionGameState(Exception e) throws Exception {
        List<ExceptionStateConnection> GameStateConnections = stateMachine.getExceptionConnectionsOf(currentGameState);
        callback.ifPresent(c -> c.exceptionConnectionChoice(currentGameState, GameStateConnections, context));
        for (ExceptionStateConnection c : GameStateConnections) {
            if (c.checkExpression(e)) {
                return c.getResultState();
            }
        }
        return null;
    }

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
