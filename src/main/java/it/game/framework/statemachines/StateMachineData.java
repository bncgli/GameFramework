package it.game.framework.statemachines;

import it.game.framework.contexts.GameContext;
import it.game.framework.states.GameState;
import it.game.framework.states.library.GameStateConnection;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class StateMachineData<C extends GameContext> implements Serializable {
    private GameState<C> startState;
    private List<GameState<C>> states;
    private List<GameStateConnection<C>> connections;

    public StateMachineData(GameState<C> startState, List<GameState<C>> states, List<GameStateConnection<C>> connections) {
        this.startState = startState;
        this.states = states;
        this.connections = connections;
    }
}