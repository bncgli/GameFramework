package it.vegas.gameframework.states.library.executors;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.library.executors.interfaces.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is intended for debugging purpose only,
 * it contains the monitor interface that contains as series
 * of abstract method that can be implemented to monitor the execution
 * of the statemachine. The class can used to monitor what happen when a specific
 * state is active to track bugs.
 * @param <C> The context class that has to extend GameContext
 */
@Getter
@Setter
@Slf4j
public class MonitorableExecutor<C extends GameContext> extends GameState<C> {

    protected final GameState<C> startingState;
    @Setter
    protected GameState<C> currentState;
    protected ExecutorMonitor monitor;

    public MonitorableExecutor(ExecutorMonitor monitor, GameState<C> startingState) {
        this.monitor = monitor;
        this.startingState = startingState;
    }

    /**
     * Execute overrides the GameState method.
     * This method iters the state machine until the end of the GameStates
     * or until a GameState returns an error. Each step is sended to
     * the monitor interface.
     */
    @Override
    public void execute() {
        currentState = startingState;
        try {
            monitor.beforeLoop(currentState);
            while (currentState != null) {
                log.info("Entering gameState: {}", currentState.getName());
                monitor.beforeExecution(currentState);

                currentState.execute();

                monitor.afterExecution(currentState);

                log.info("Exiting gameState: {}", currentState.getName());

                GameState<C> nextGameState = currentState.getNextGameState();

                monitor.nextSelectedGamestate(currentState, nextGameState);

                currentState = nextGameState;
            }
            monitor.afterLoop(currentState);
        } catch (Exception e) {
            monitor.caughtException(currentState, e);
            log.error(
                    "Error occurred executing GameState {} with errorID {} and message:\n{}",
                    currentState,
                    e.hashCode(),
                    e.getMessage()
            );
        }
    }


}