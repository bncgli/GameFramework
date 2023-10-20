package it.game.framework.builders;

import it.game.framework.exceptions.ExceptionLibrary;
import it.game.framework.exceptions.GameException;
import it.game.framework.stateconnections.DirectStateConnection;
import it.game.framework.stateconnections.ExceptionStateConnection;
import it.game.framework.stateconnections.GameStateConnection;
import it.game.framework.stateconnections.interfaces.Expression;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;

/**
 * This structure helps to the creation of the machine
 * step-by-step. Based on the builder design patterns
 * creates states and connections alike and add them to the
 * state machine object
 */
@Slf4j
public class StepBuilder {

    /**
     * Reference to the state machine that has to be built
     */
    protected final StateMachine machine;

    /**
     * Reference to the last game state for reference with the
     * methods addConnectionFromLastState and addTrueGameConnectionFromLastState
     */
    private GameState last;

    /**
     * The builder method that starts the construction process
     * @param machine The reference to the state machine that has to be built
     * @return A new instance of StepBuilder
     */
    public static StepBuilder builder(StateMachine machine) {
        return new StepBuilder(machine);
    }

    private StepBuilder(StateMachine machine) {
        this.machine = machine;
        this.last = null;
    }

    /**
     * This method adds the game state given as parameter
     * as starting state in the machine
     * @param GameState The GameState elected as starting state of the machine
     * @return This instance of the StepBuilder
     */
    public StepBuilder addStartingState(GameState GameState) {
        if (!machine.getStates().contains(GameState)) {
            machine.getStates().add(0, GameState);
        }
        machine.setStartState(GameState);
        last = GameState;
        return this;
    }

    /**
     * This method add a GameState to the list of
     * states in the machine
     * @param GameState The GameState to be added
     * @return This instance of the StepBuilder
     */
    public StepBuilder addGameState(GameState GameState) {
        machine.getStates().add(GameState);
        last = GameState;
        return this;
    }

    /**
     * This method adds a GameStateConnection to the
     * list of connections in the state machine
     * @param GameStateConnection The GameStateConnection to be added
     * @return This instance of the StepBuilder
     */
    public StepBuilder addConnection(GameStateConnection GameStateConnection) {
        machine.getConnections().add(GameStateConnection);
        return this;
    }

    /**
     * This method creates and add a new GameStateConnection to
     * the lists of connections in the state machine with the
     * parameters passed by the user
     * @param expressionDescription Is a short description of the expression to show inside the machine's renderings
     * @param expression The expression lambda that define the connection this lambda has one argument(the context) and returns a boolean
     * @param startingState The state where the connection start from
     * @param resultState The state where the connection ends to
     * @return This instance of teh StepBuilder
     */
    public StepBuilder addConnection(String expressionDescription, Expression expression, GameState startingState, GameState resultState) {
        return addConnection(
                new GameStateConnection(
                        expressionDescription,
                        startingState,
                        expression,
                        resultState
                )
        );
    }

    /**
     * This method creates and add a DirectGameStateConnection
     * to the state machine. This type of connection
     * IS ALWAYS TRUE, so it is useful when the machine has only one
     * connection or for last resource if ALL THE PREVIOUS connections
     * returned false
     * @param startingState The state where the connection start from
     * @param resultState The state where the connection end to
     * @return This instance of StepBuilder
     */
    public StepBuilder addDirectConnection(GameState startingState, GameState resultState) {
        return addConnection(
                new DirectStateConnection(
                        startingState,
                        resultState
                )
        );
    }

    /**
     * This method creates and add an ExceptionGameStateConnection,
     * this kind of state is checked only if an exception is caught
     * during the execution of a state, if the caught exception
     * coincide with the caught exception contained in the connection
     * the executor moves to this resulting state.
     * @param expressionDescription Is a short description of the expression to show inside the machine's renderings
     * @param exception The exception to catch
     * @param startingState The state where the connection start from
     * @param resultState The state where the connection ends to
     * @return This instance of teh StepBuilder
     */
    public StepBuilder addExceptionConnection(String expressionDescription, Exception exception, GameState startingState, GameState resultState) {
        return addConnection(
                new ExceptionStateConnection(
                        expressionDescription,
                        startingState,
                        exception,
                        resultState
                )
        );
    }

    /**
     * This method creates and add a new GameStateConnection to
     * the lists of connections in the state machine with the
     * parameters passed by the user. The starting state is the last state added
     * to the machine
     * @param expressionDescription Is a short description of the expression to show inside the machine's renderings
     * @param expression The expression lambda that define the connection this lambda has one argument(the context) and returns a boolean
     * @param resultState The state where the connection ends to
     * @return This instance of teh StepBuilder
     */
    public StepBuilder addConnectionFromLastState(String expressionDescription, Expression expression, GameState resultState) {
        return addConnection(expressionDescription, expression, last, resultState);
    }

    /**
     * This method creates and add a DirectGameStateConnection
     * to the state machine. This type of connection
     * IS ALWAYS TRUE, so it is useful when the machine has only one
     * connection or for last resource if ALL THE PREVIOUS connections
     * returned false. The starting state is the last GameState added to
     * the machine
     * @param resultState The state where the connection end to
     * @return This instance of StepBuilder
     */
    public StepBuilder addDirectConnectionFromLastState(GameState resultState) {
        return addDirectConnection(last, resultState);
    }

    /**
     * This method creates and add an ExceptionGameStateConnection,
     * this kind of state is checked only if an exception is caught
     * during the execution of a state, if the caught exception
     * coincide with the caught exception contained in the connection
     * the executor moves to this resulting state.
     * The starting state is the last state added to the machine
     * @param expressionDescription Is a short description of the expression to show inside the machine's renderings
     * @param exception The exception to catch
     * @param resultState The state where the connection ends to
     * @return This instance of teh StepBuilder
     */
    public StepBuilder addExceptionConnectionFromLastState(String expressionDescription, Exception exception, GameState resultState) {
        return addExceptionConnection(expressionDescription, exception, last, resultState);
    }

    /**
     * This method adds a GameStateConnection to the
     * list of global connections in the state machine.
     * Global connection can start from any state and are generally
     * used to handle unexpected results.
     * @param GameStateConnection The GameStateConnection to be added
     * @return This instance of the StepBuilder
     */
    public StepBuilder addGlobalConnection(GameStateConnection GameStateConnection) {
        machine.getGlobalConnections().add(GameStateConnection);
        return this;
    }


    /**
     * This method creates and adds a GameStateConnection to the
     * list of global connections in the state machine.
     * Global connection can start from any state and are generally
     * used to handle unexpected results.
     * @param expressionDescription Short description of the expression for rendering purposes only
     * @param expression The expression lambda that define the connection this lambda has one argument(the context) and returns a boolean
     * @param resultState The state where the connection ends to
     * @return This instance of the StepBuilder
     */
    public StepBuilder addGlobalConnection(String expressionDescription, Expression expression, GameState resultState) {
        return addGlobalConnection(
                new GameStateConnection(
                        expressionDescription,
                        null,
                        expression,
                        resultState
                )
        );
    }

    /**
     * This method creates and add an ExceptionGameStateConnection,
     * this kind of state is checked only if an exception is caught
     * during the execution of a state, if the caught exception
     * coincide with the caught exception contained in the connection
     * the executor moves to this resulting state.
     * Global connection can start from any state and are generally
     * used to handle unexpected results.
     * @param expressionDescription Is a short description of the expression to show inside the machine's renderings
     * @param exception The exception to catch
     * @param resultState The state where the connection ends to
     * @return This instance of teh StepBuilder
     */
    public StepBuilder addGlobalExceptionConnection(String expressionDescription, Exception exception, GameState resultState) {
        return addGlobalConnection(
                new ExceptionStateConnection(
                        expressionDescription,
                        null,
                        exception,
                        resultState
                )
        );
    }

    /**
     * The last method to be used to finalize the
     * modifications of StepBuilder, this method
     * checks the possible errors inside the machine
     * and build it.
     */
    public void build() {
        log.info("Building machine...");
        try {
            log.info("Checking starting state");
            if (machine.getStartState() == null) {
                throw new GameException(ExceptionLibrary.get("STARTING_STATE_IS_NULL"));
            }
            if (!machine.getStates().contains(machine.getStartState())) {
                throw new GameException(ExceptionLibrary.get("STARTING_STATE_IS_NOT_IN_MACHINE_STATES"));
            }

            log.info("Checking duplicates in states list");
            if (machine.getStates().size() > new HashSet<>(machine.getStates()).size()) {
                log.warn("Machine's state list contains unnecessary duplicates");
            }

            log.info("Checking direct connections as globals");
            for(GameStateConnection c : machine.getGlobalConnections()){
                if(c instanceof DirectStateConnection)
                    throw new GameException(ExceptionLibrary.get("DIRECT_CONNECTION_IN_GLOBALS"), String.format("connection: %s", c));
            }

            log.info("Checking direct connection not in last place");
            for (GameState s : machine.getStates()) {
                List<GameStateConnection> connectionsOf = machine.getConnectionsOf(s);
                for (int i = 0; i < connectionsOf.size(); i++) {
                    if (connectionsOf.get(i) instanceof DirectStateConnection) {
                        if (connectionsOf.size() - 1 > i) {
                            throw new GameException(ExceptionLibrary.get("DIRECT_CONNECTION_IS_NOT_LAST"), String.format("GameState: %s, connection: %s", s.ID(), connectionsOf.get(i)));
                        }
                    }
                }
            }

            log.info("Checking connections consistency");
            for (GameStateConnection c : machine.getConnections()) {
                if (c.getStartingState() == null) {
                    throw new GameException(ExceptionLibrary.get("CONNECTION_STARTINGSTATE_IS_NULL"), c.toString());
                }
                if (!machine.getStates().contains(c.getStartingState()) || !machine.getStates().contains(c.getResultState())) {
                    throw new GameException(ExceptionLibrary.get("CONNECTION_STATE_IS_NOT_IN_MACHINE_STATES"), c.toString());
                }
            }

            log.info("Checking global connections consistency");
            for (GameStateConnection c : machine.getGlobalConnections()) {
                if (!machine.getStates().contains(c.getResultState())) {
                    throw new GameException(ExceptionLibrary.get("CONNECTION_STATE_IS_NOT_IN_MACHINE_STATES"), c.toString());
                }
            }
        } catch (Exception e) {
            log.error(GameException.format(e));
        }
        log.info("Machine built!");
    }

}
