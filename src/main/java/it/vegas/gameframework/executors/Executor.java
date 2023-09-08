package it.vegas.gameframework.executors;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.library.structures.GameStateCondition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


import java.util.List;


/**
 * The executor class iter and activate the statemachine.
 * The executor class extends GameState so it can be used as a single GameState in a statemachine.
 * It takes the first GameState of the statemachine and execute it, then
 * It chooses the first GameStateCondition that returns true end sets it
 * as currentState and starts over until there are no more GameStates
 * @param <C> The context class that extends GameContext
 */
@Getter
@Slf4j
public class Executor<C extends GameContext> extends GameState<C> {

    protected final GameState<C> startingState;
    @Setter
    protected GameState<C> currentState;


    public Executor(GameState<C> startingState) {
        this.startingState = startingState;
    }

    /**
     * Execute overrides the GameState method.
     * This method iters the state machine until the end of the GameState
     * or until a GameState returns an error
     * @return
     */
    @Override
    public void execute() {
        currentState = startingState;
        try {
            while (currentState != null) {
                log.info("Entering gameState: {}", currentState);
                currentState.execute();
                log.info("Exiting gameState: {}", currentState);
                currentState = getNextGameState(currentState);
            }
        } catch (Exception e) {
            log.error(
                    "Error occurred executing GameState {} with errorID {} and message:\n{}",
                    currentState,
                    e.hashCode(),
                    e.getMessage()
            );
        }
    }

    protected GameState<C> getNextGameState(GameState<C> state) {
        List<GameStateCondition<C>> gameStateConditions = state.getNextGameStates();
        for (GameStateCondition<C> c : gameStateConditions) {
            if (c.getExpression(state)) {
                return c.getResultState();
            }
        }
        return null;
    }


}
