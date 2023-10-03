package it.game.framework.executors;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import it.game.framework.stateconnections.GameStateConnection;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * The executor class iter and activate the statemachine.
 * The executor class extends GameState so it can be used as a single GameState in a statemachine.
 * It takes the first GameState of the statemachine and execute it, then
 * It chooses the first GameStateConnection that returns true end sets it
 * as currentState and starts over until there are no more GameStates
 *
 */
@Getter
@Slf4j
public class GameExecutor {

    @Setter
    protected StateMachine stateMachine;

    protected GameContext context;
    protected GameState currentGameState;

    public GameExecutor(StateMachine stateMachine, GameContext context) {
        this.stateMachine = stateMachine;
        this.context = context;
        this.currentGameState = null;
    }

    public static void execute(StateMachine machine, GameContext context){
        new GameExecutor(machine, context).execute();
    }

    /**
     * Execute overrides the GameState method.
     * This method iters the state machine until the end of the GameState
     * or until a GameState returns an error
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
                log.info("Entering GameState: {}", currentGameState.getName());
                currentGameState.execute(context);
                log.info("Exiting GameState: {}", currentGameState.getName());
                currentGameState = getNextGameState();
            }
        } catch (Exception e) {
            log.error(GameException.format(e, currentGameState.getName()));
        }
        log.info("Ending state machine execution");
    }


    /**
     * Iters all the GameStateconditions and return the game state of
     * the FIRST game state condition returning TRUE
     *
     * @return The GameState with the condition that returned true
     */
    protected GameState getNextGameState() throws Exception {
        List<GameStateConnection> GameStateConnections = stateMachine.getConnectionsOf(currentGameState);
        for (GameStateConnection c : GameStateConnections) {
            if (c.checkExpression(context)) {
                return c.getResultState();
            }
        }
        return null;
    }

}
