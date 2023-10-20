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
 * This class leaves to the user the execution of the
 * machine by repeatedly call the execute method.
 * The class extends iterable and iterator to grant the possibility
 * to execute the machine into a foreach loop.
 */
@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class SteppedExecutor extends GameExecutor implements Iterable<Optional<GameState>>, Iterator<Optional<GameState>> {


    /**
     * The execution phases; <br>
     * Start, the beginning of the machine execution<br>
     * Loop, the loop where the GameStates are explored and executed<br>
     * End, the end of execution, when in this the current state is null
     */
    public enum Steps {
        START, LOOP, END;
    }

    @Value("${game.framework.executor.global_blocking_exception}")
    protected boolean GlobalExecutionExceptionBlocking;
    @Value("${game.framework.executor.stepped_executor_blocking_exception}")
    private boolean thisExecutionExceptionBlocking;
    private Steps currentStep = Steps.START;

    /**
     * Resets the execution of the machine
     */
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