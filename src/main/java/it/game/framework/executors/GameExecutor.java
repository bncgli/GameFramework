package it.game.framework.executors;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import it.game.framework.states.library.GameStateConnection;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * The executor class iter and activate the statemachine.
 * The executor class extends GameState<C> so it can be used as a single GameState<C> in a statemachine.
 * It takes the first GameState<C> of the statemachine and execute it, then
 * It chooses the first GameStateConnection that returns true end sets it
 * as currentState and starts over until there are no more GameState<C>s
 *
 * @param <C> The context class that extends GameContext
 */
@Getter
@Slf4j
public class GameExecutor<C extends GameContext> {

    @Setter
    protected StateMachine<C> stateMachine;
    protected GameState<C> currentGameState;

    public GameExecutor() {
        this(null);
    }

    public GameExecutor(StateMachine<C> stateMachine) {
        this.stateMachine = stateMachine;
        this.currentGameState = null;
    }

    /**
     * Execute overrides the GameState<C> method.
     * This method iters the state machine until the end of the GameState<C>
     * or until a GameState<C> returns an error
     */
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
            while (currentGameState != null) {
                log.info("Entering GameState<C>: {}", currentGameState.getName());
                currentGameState.execute(stateMachine.getContext());
                log.info("Exiting GameState<C>: {}", currentGameState.getName());
                currentGameState = getNextGameState();
            }
        } catch (Exception e) {
            log.error(GameException.format(e, currentGameState.getName()));
        }
        log.info("Ending state machine execution");
    }


    /**
     * Iters all the GameState<C>conditions and return the game state of
     * the FIRST game state condition returning TRUE
     *
     * @return The GameState<C> with the condition that returned true
     */
    protected GameState<C> getNextGameState() {
        List<GameStateConnection<C>> GameStateConnections = stateMachine.getConnectionsOf(currentGameState);
        for (GameStateConnection<C> c : GameStateConnections) {
            if (c.getExpression(stateMachine.getContext())) {
                return c.getResultState();
            }
        }
        return null;
    }

}
