package it.game.framework.executors;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class SteppedExecutor extends GameExecutor implements Iterable<Optional<GameState>>, Iterator<Optional<GameState>> {

    public enum Steps {
        START, LOOP, END;
    }

    @Value("${game.framework.executor.global_blocking_exception}")
    protected boolean GlobalExecutionExceptionBlocking;
    @Value("${game.framework.executor.stepped_executor_blocking_exception}")
    private boolean thisExecutionExceptionBlocking;
    Steps currentStep = Steps.START;

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
                    log.info("Executing state machine");
                    begin();
                    currentStep = Steps.LOOP;
                    break;
                case LOOP:
                    process();
                    if (currentGameState == null) currentStep = Steps.END;
                    break;
                case END:
                    log.info("Ending state machine execution");
                    end();
            }
        } catch (Exception e) {
            log.error(GameException.format(e, currentGameState.getName()));
        }
    }

    @Override
    public boolean hasNext() {
        return currentStep != Steps.END;
    }

    @Override
    public Optional<GameState> next() {
        execute();
        return Optional.ofNullable(currentGameState);
    }

    @Override
    public Iterator<Optional<GameState>> iterator() {
        return this;
    }
}