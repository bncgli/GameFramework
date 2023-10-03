package it.game.framework.executors;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Optional;

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
public class SteppedExecutor extends GameExecutor implements Iterable<Optional<GameState>> {

    @AllArgsConstructor
    private class SteppedExecutorIterator implements Iterator<Optional<GameState>> {
        SteppedExecutor executor;

        @Override
        public boolean hasNext() {
            return executor.currentStep != Steps.END;
        }

        @Override
        public Optional<GameState> next() {
            executor.execute();
            return Optional.ofNullable(executor.currentGameState);
        }
    }

    public enum Steps {
        START, LOOP, END;
    }

    Steps currentStep = Steps.START;

    public SteppedExecutor(StateMachine stateMachine, GameContext context) {
        super(stateMachine, context);
    }



    public void restart() {
        currentStep = Steps.START;
    }

    /**
     * Execute overrides the execute method.
     * This method executes the state machine one state a the time
     * or until a GameState returns an error. Everytime is called it executes
     * a new state.
     */
    @Override
    public void execute() {
        try {
            switch (currentStep) {
                case START:
                    if (stateMachine == null) {
                        throw new GameException(GameExceptionsLibrary.STATEMACHINE_IS_NULL);
                    }
                    log.info("Executing state machine");
                    if (stateMachine.getStartState() == null) {
                        throw new GameException(GameExceptionsLibrary.STARTING_STATE_IS_NULL);
                    }
                    currentGameState = stateMachine.getStartState();
                    currentStep = Steps.LOOP;
                    break;
                case LOOP:
                    log.info("Entering GameState: {}", currentGameState.getName());
                    currentGameState.execute(context);
                    log.info("Exiting GameState: {}", currentGameState.getName());
                    currentGameState = getNextGameState();
                    if (currentGameState == null) currentStep = Steps.END;
                    break;
                case END:
                    log.info("Ending state machine execution");
            }
        } catch (Exception e) {
            log.error(GameException.format(e, currentGameState.getName()));
        }
    }

    @Override
    public Iterator<Optional<GameState>> iterator() {
        return new SteppedExecutorIterator(this);
    }
}