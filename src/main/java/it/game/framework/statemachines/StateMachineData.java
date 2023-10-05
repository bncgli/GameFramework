package it.game.framework.statemachines;

import it.game.framework.stateconnections.GameStateConnection;
import it.game.framework.states.GameState;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StateMachineData implements Serializable {
    private GameState startState;
    private List<GameState> states;
    private List<GameStateConnection> connections;
    private List<GameStateConnection> globalConnections;


    public StateMachineData(StateMachine machine) {
        this.startState = machine.getStartState();
        this.states = new ArrayList<>(machine.getStates());
        this.connections = new ArrayList<>(machine.getConnections());
        this.globalConnections = new ArrayList<>(machine.getGlobalConnections());
    }

    public void populate(StateMachine machine){
        machine.setStartState(startState);
        machine.setStates(states);
        machine.setConnections(connections);
        machine.setGlobalConnections(globalConnections);
    }
}