package it.game.framework.builders;

import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;
import it.game.framework.stateconnections.expressions.DirectExpression;
import it.game.framework.stateconnections.interfaces.Expression;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import it.game.framework.stateconnections.GameStateConnection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class StepBuilder {

    protected final StateMachine machine;
    private GameState last;

    public static StepBuilder builder(StateMachine machine){
        return new StepBuilder(machine);
    }

    public StepBuilder(StateMachine machine) {
        this.machine = machine;
        this.last = null;
    }

    public StepBuilder addStartingState(GameState GameState) {
        if (!machine.getStates().contains(GameState)) {
            machine.getStates().add(0,GameState);
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
        GameStateConnection connection = GameStateConnection.create(
                expressionDescription,
                startingState,
                expression,
                resultState
        );
        machine.getConnections().add(connection);
        return this;
    }

    public StepBuilder addConnection(GameState startingState, GameState resultState) {
        GameStateConnection connection = GameStateConnection.createDirect(
                startingState,
                resultState
        );
        machine.getConnections().add(connection);
        return this;
    }

    public StepBuilder addConnectionFromLastState(String expressionDescription, Expression expression, GameState resultState) {
        GameStateConnection connection = GameStateConnection.create(
                expressionDescription,
                last,
                expression,
                resultState
        );
        machine.getConnections().add(connection);
        return this;
    }

    public StepBuilder addConnectionFromLastState(GameState resultState) {
        GameStateConnection connection = GameStateConnection.createDirect(
                last,
                resultState
        );
        machine.getConnections().add(connection);
        return this;
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
            log.info("Checking direct connection not in last place");
            for(GameState s: machine.getStates()){
                List<GameStateConnection> connectionsOf = machine.getConnectionsOf(s);
                for (int i = 0; i < connectionsOf.size(); i++) {
                    if(connectionsOf.get(i).getExpression() instanceof DirectExpression){
                        if(connectionsOf.size() - 1 > i){
                            throw new GameException(GameExceptionsLibrary.DIRECT_EXPRESSION_IS_NOT_LAST, String.format("GameState: %s, connection: %s", s.ID(), connectionsOf.get(i)));
                        }
                    }
                }
            }
            log.info("Checking connections consistency");
            for (GameStateConnection c : machine.getConnections()) {
                if (c.getStartingState() == null) {
                    throw new GameException(GameExceptionsLibrary.CONNECTION_STARTINGSTATE_IS_NULL, c.toString());
                }
                if (c.getResultState() == null) {
                    log.warn("Connection's Result state is null the machine will end execution unexpectedly");
                }
                if (!machine.getStates().contains(c.getStartingState()) || !machine.getStates().contains(c.getResultState())) {
                    throw new GameException(GameExceptionsLibrary.CONNECTION_STATE_IS_NOT_IN_MACHINE_STATES, c.toString());
                }
            }
        } catch (Exception e) {
            log.error(GameException.format(e));
        }
        log.info("Machine built!");
    }

}