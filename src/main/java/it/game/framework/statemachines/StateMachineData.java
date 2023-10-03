package it.game.framework.statemachines;

import it.game.framework.states.GameState;
import it.game.framework.stateconnections.GameStateConnection;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class StateMachineData implements Serializable {
    private GameState startState;
    private List<GameState> states;
    private List<GameStateConnection> connections;

    public StateMachineData(GameState startState, List<GameState> states, List<GameStateConnection> connections) {
        this.startState = startState;
        this.states = states;
        this.connections = connections;
    }
}