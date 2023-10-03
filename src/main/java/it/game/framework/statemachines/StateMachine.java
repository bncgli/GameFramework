package it.game.framework.statemachines;

import it.game.framework.serializations.Deserializer;
import it.game.framework.serializations.Serializer;
import it.game.framework.statemachines.interfaces.IterationAction;
import it.game.framework.states.GameState;
import it.game.framework.stateconnections.GameStateConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class StateMachine {
    
    private StateMachineData data;

    public StateMachine(GameState startState, List<GameState> states, List<GameStateConnection> connections) {
        this.data = new StateMachineData(startState, states, connections);
    }

    public StateMachine() {
        this(
                null,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public List<GameStateConnection> getConnections() {
        return data.getConnections();
    }

    public GameState getStartState() {
        return data.getStartState();
    }

    public List<GameState> getStates() {
        return data.getStates();
    }

    public void setStartState(GameState startState) {
        data.setStartState(startState);
    }

    public void setStates(List<GameState> states) {
        data.setStates(states);
    }

    public void setConnections(List<GameStateConnection> GameStateConnections) {
        data.setConnections(GameStateConnections);
    }

    public List<GameStateConnection> getConnectionsOf(GameState GameState) {
        return getConnections().stream().filter(v -> v.getStartingState().equals(GameState)).collect(Collectors.toList());
    }

    public void saveTo(String filename) {
        Serializer.save(data, filename);
    }

    public void loadFrom(String filename) {
        data = Deserializer.load(filename);
    }

    public void apply(IterationAction action) {
        getStates().forEach(action::execute);
    }


}
