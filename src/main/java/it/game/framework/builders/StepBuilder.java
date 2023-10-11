package it.game.framework.builders;

import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;
import it.game.framework.stateconnections.DirectStateConnection;
import it.game.framework.stateconnections.ExceptionStateConnection;
import it.game.framework.stateconnections.GameStateConnection;
import it.game.framework.stateconnections.interfaces.Expression;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;

@Slf4j
public class StepBuilder {

    protected final StateMachine machine;
    private GameState last;

    public static StepBuilder builder(StateMachine machine) {
        return new StepBuilder(machine);
    }

    private StepBuilder(StateMachine machine) {
        this.machine = machine;
        this.last = null;
    }

    public StepBuilder addStartingState(GameState GameState) {
        if (!machine.getStates().contains(GameState)) {
            machine.getStates().add(0, GameState);
        }
        machine.setStartState(GameState);
        last = GameState;
        return this;
    }

    public StepBuilder addGameState(GameState GameState) {
        machine.getStates().add(GameState);
        last = GameState;
        return this;
    }

    public StepBuilder addConnection(GameStateConnection GameStateConnection) {
        machine.getConnections().add(GameStateConnection);
        return this;
    }


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

    public StepBuilder addTrueConnection(GameState startingState, GameState resultState) {
        return addConnection(
                new DirectStateConnection(
                        startingState,
                        resultState
                )
        );
    }

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


    public StepBuilder addConnectionFromLastState(String expressionDescription, Expression expression, GameState resultState) {
        return addConnection(expressionDescription, expression, last, resultState);
    }

    public StepBuilder addTrueConnectionFromLastState(GameState resultState) {
        return addTrueConnection(last, resultState);
    }

    public StepBuilder addExceptionConnectionFromLastState(String expressionDescription, Exception exception, GameState resultState) {
        return addExceptionConnection(expressionDescription, exception, last, resultState);
    }

    public StepBuilder addGlobalConnection(GameStateConnection GameStateConnection) {
        machine.getGlobalConnections().add(GameStateConnection);
        return this;
    }

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

    public void build() {
        log.info("Building machine...");
        try {
            log.info("Checking starting state");
            if (machine.getStartState() == null) {
                throw new GameException(GameExceptionsLibrary.STARTING_STATE_IS_NULL);
            }
            if (!machine.getStates().contains(machine.getStartState())) {
                throw new GameException(GameExceptionsLibrary.STARTING_STATE_IS_NOT_IN_MACHINE_STATES);
            }

            log.info("Checking duplicates in states list");
            if (machine.getStates().size() > new HashSet<>(machine.getStates()).size()) {
                log.warn("Machine's state list contains unnecessary duplicates");
            }

            log.info("Checking direct connections as globals");
            for(GameStateConnection c : machine.getGlobalConnections()){
                if(c instanceof DirectStateConnection)
                    throw new GameException(GameExceptionsLibrary.DIRECT_CONNECTION_IN_GLOBALS, String.format("connection: %s", c));
            }

            log.info("Checking direct connection not in last place");
            for (GameState s : machine.getStates()) {
                List<GameStateConnection> connectionsOf = machine.getConnectionsOf(s);
                for (int i = 0; i < connectionsOf.size(); i++) {
                    if (connectionsOf.get(i) instanceof DirectStateConnection) {
                        if (connectionsOf.size() - 1 > i) {
                            throw new GameException(GameExceptionsLibrary.DIRECT_CONNECTION_IS_NOT_LAST, String.format("GameState: %s, connection: %s", s.ID(), connectionsOf.get(i)));
                        }
                    }
                }
            }

            log.info("Checking connections consistency");
            for (GameStateConnection c : machine.getConnections()) {
                if (c.getStartingState() == null) {
                    throw new GameException(GameExceptionsLibrary.CONNECTION_STARTINGSTATE_IS_NULL, c.toString());
                }
                if (!machine.getStates().contains(c.getStartingState()) || !machine.getStates().contains(c.getResultState())) {
                    throw new GameException(GameExceptionsLibrary.CONNECTION_STATE_IS_NOT_IN_MACHINE_STATES, c.toString());
                }
            }

            log.info("Checking global connections consistency");
            for (GameStateConnection c : machine.getGlobalConnections()) {
                if (!machine.getStates().contains(c.getResultState())) {
                    throw new GameException(GameExceptionsLibrary.CONNECTION_STATE_IS_NOT_IN_MACHINE_STATES, c.toString());
                }
            }
        } catch (Exception e) {
            log.error(GameException.format(e));
        }
        log.info("Machine built!");
    }

}
