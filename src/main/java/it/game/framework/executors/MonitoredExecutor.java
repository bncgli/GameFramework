package it.game.framework.executors;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;
import it.game.framework.executors.interfaces.ExecutorMonitor;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is intended for debugging purpose only,
 * it contains the monitor interface that contains as series
 * of abstract method that can be implemented to monitor the execution
 * of the statemachine. The class can used to monitor what happen when a specific
 * state is active to track bugs.
 *
 */
@Getter
@Setter
@Slf4j
public class MonitoredExecutor extends GameExecutor {

    protected ExecutorMonitor monitor;

    public MonitoredExecutor(ExecutorMonitor monitor, StateMachine stateMachine, GameContext context) {
        super(stateMachine, context);
        this.monitor = monitor;
    }

    public static void execute(ExecutorMonitor monitor,StateMachine machine, GameContext context){
        new MonitoredExecutor(monitor,machine, context).execute();
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
            if (stateMachine == null) {
                throw new GameException(GameExceptionsLibrary.STATEMACHINE_IS_NULL);
            }
            log.info("Executing state machine");
            if (stateMachine.getStartState() == null) {
                throw new GameException(GameExceptionsLibrary.STARTING_STATE_IS_NULL);
            }
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

}