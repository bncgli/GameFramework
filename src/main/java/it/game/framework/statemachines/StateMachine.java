package it.game.framework.statemachines;

import it.game.framework.serializations.Serializer;
import it.game.framework.stateconnections.ExceptionStateConnection;
import it.game.framework.stateconnections.GameStateConnection;
import it.game.framework.statemachines.interfaces.IterationAction;
import it.game.framework.states.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This is the main class of the library, this one stores
 * GameStates and GameStateConnections in a neat way to be handled,
 * executed and stored/loaded
 */
@Slf4j
@Setter
@Getter
@AllArgsConstructor
public class StateMachine {

    private GameState startState;
    private List<GameState> states;
    private List<GameStateConnection> connections;
    private List<GameStateConnection> globalConnections;

    public StateMachine() {
        this(
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    /**
     * Returns all the connections of the given GameState,
     * first the global ones then the ones that start from the given GameState
     * @param gameState The target GameState
     * @return The list of GameStateConnections first the global ones then the ones that
     * start from the given GameState
     */
    public List<GameStateConnection> getConnectionsOf(GameState gameState) {
        List<GameStateConnection> ret = new ArrayList<>();
        ret.addAll(globalConnections);
        ret.addAll(getConnections().stream().filter(v ->
                v.getStartingState().equals(gameState)
        ).collect(Collectors.toList()));
        return ret;
    }

    /**
     * Returns all the ExceptionGameStateConnections of the given GameState,
     * first the global ones then the ones that start from the given GameState
     * @param gameState The target GameState
     * @return The list of ExceptionGameStateConnections first the global ones then the ones that
     * start from the given GameState
     */
    public List<ExceptionStateConnection> getExceptionConnectionsOf(GameState gameState) {
        return getConnectionsOf(gameState).stream().filter(v ->
                        v instanceof ExceptionStateConnection
                ).map(ExceptionStateConnection.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Serialize the data of the StateMachine into a gfobject file
     * @param filename The name of the gfobject file without extension
     */
    public void saveTo(String filename) {
        Serializer.save(new StateMachineData(this), filename);
    }

    /**
     * Deserialize the data of the StateMachine from a gfobject file
     * @param filename The name of the gfobject file without extension
     */
    public void loadFrom(String filename) {
        StateMachineData loaded = Serializer.load(filename);
        assert loaded != null;
        loaded.populate(this);
    }

    /**
     * Utility method that executes a lambda on every GameState of the machine
     * @param action The lambda formula to execute
     */
    public void apply(IterationAction action) {
        getStates().forEach(action::execute);
    }


}
