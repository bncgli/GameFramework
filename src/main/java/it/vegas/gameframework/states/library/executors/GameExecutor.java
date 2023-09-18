package it.vegas.gameframework.states.library.executors;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.exceptions.GameException;
import it.vegas.gameframework.states.GameState;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
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
public class GameExecutor<C extends GameContext> extends GameState<C> {

    protected final GameState<C> startingState;
    @Setter
    protected GameState<C> currentState;
    protected List<GameState<C>> visitedStates;

    public GameExecutor(GameState<C> startingState) {
        this.startingState = startingState;
        visitedStates = new LinkedList<>();
    }

    /**
     * Execute overrides the GameState method.
     * This method iters the state machine until the end of the GameState
     * or until a GameState returns an error
     */
    @Override
    public void execute() {
        log.info("Starting executor: {}", this.getName());
        currentState = startingState;
        try {
            while (currentState != null) {
                log.info("Entering gameState: {}", currentState.getName());
                currentState.execute();
                log.info("Exiting gameState: {}", currentState.getName());
                visitedStates.add(currentState);
                currentState = currentState.getNextGameState();
            }
        } catch (Exception e) {
            log.error(GameException.format(e, currentState.getName()));
        }
        log.info("Ending executor: {}", this.getName());
    }

}