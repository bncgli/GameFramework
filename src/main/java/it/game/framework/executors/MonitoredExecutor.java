package it.game.framework.executors;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;
import it.game.framework.executors.interfaces.ExecutorMonitor;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is intended for debugging purpose only,
 * it contains the monitor interface that contains as series
 * of abstract method that can be implemented to monitor the execution
 * of the statemachine. The class can used to monitor what happen when a specific
 * state is active to track bugs.
 *
 */
@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class MonitoredExecutor extends GameExecutor {

    protected ExecutorMonitor monitor;

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
            currentGameState = stateMachine.getStartState();
            monitor.beforeLoop();
            while (currentGameState != null) {
                log.info("Entering GameState: {}", currentGameState.getName());
                monitor.beforeExecution(currentGameState);
                currentGameState.execute(context);
                monitor.afterExecution(currentGameState);
                log.info("Exiting GameState: {}", currentGameState.getName());
                GameState nextGameState = getNextGameState();
                monitor.nextSelectedGameState(currentGameState, nextGameState);
                currentGameState = nextGameState;
            }
            monitor.afterLoop();
        } catch (Exception e) {
            monitor.caughtException(currentGameState, e);
            log.error(GameException.format(e, currentGameState.getName()));
        }
        log.info("Ending state machine execution");
    }

    @Override
    protected void executionChecks() throws GameException {
        if (monitor == null){
            throw new GameException(GameExceptionsLibrary.EXECUTOR_MONITOR_IS_NULL);
        }
        super.executionChecks();
    }
}