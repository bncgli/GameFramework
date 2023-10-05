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

    public List<GameStateConnection> getConnectionsOf(GameState gameState) {
        List<GameStateConnection> ret = new ArrayList<>();
        ret.addAll(globalConnections);
        ret.addAll(getConnections().stream().filter(v ->
                v.getStartingState().equals(gameState)
        ).collect(Collectors.toList()));
        return ret;
    }

    public List<ExceptionStateConnection> getExceptionConnectionsOf(GameState gameState) {
        return getConnectionsOf(gameState).stream().filter(v ->
                        v instanceof ExceptionStateConnection
                ).map(ExceptionStateConnection.class::cast)
                .collect(Collectors.toList());
    }


    public void saveTo(String filename) {
        Serializer.save(new StateMachineData(this), filename);
    }

    public void loadFrom(String filename) {
        StateMachineData loaded = Serializer.load(filename);
        assert loaded != null;
        loaded.populate(this);
    }

    public void apply(IterationAction action) {
        getStates().forEach(action::execute);
    }


}
