package it.game.framework.statemachines;

import it.game.framework.builders.Builder;
import it.game.framework.contexts.GameContext;
import it.game.framework.renderers.MachineRenderer;
import it.game.framework.serializations.Deserializer;
import it.game.framework.serializations.Serializer;
import it.game.framework.statemachines.interfaces.IterationAction;
import it.game.framework.states.GameState;
import it.game.framework.executors.GameExecutor;
import it.game.framework.states.library.GameStateConnection;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class StateMachine<C extends GameContext> {

    @Getter
    @Setter
    private C context;
    private StateMachineData<C> data;

    private Builder<C> builder;
    private GameExecutor<C> executor;
    private MachineRenderer<C> renderer;

    public StateMachine(GameState<C> startState, List<GameState<C>> states, List<GameStateConnection<C>> connections, C context, Builder<C> builder, GameExecutor<C> executor, MachineRenderer<C> renderer) {
        this.data = new StateMachineData<>(startState, states, connections);
        this.context = context;
        this.builder = builder;
        this.executor = executor;
        this.renderer = renderer;
    }

    public StateMachine(C context) {
        this(
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                context,
                null,
                null,
                null
        );
        this.builder = new Builder<>(this);
        this.executor = new GameExecutor<>(this);
        this.renderer = new MachineRenderer<>(this);
    }

    public StateMachine() {
        this(
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null,
                null,
                null
        );
        this.builder = new Builder<>(this);
        this.executor = new GameExecutor<>(this);
        this.renderer = new MachineRenderer<>(this);
    }

    public void setExecutor(GameExecutor<C> executor){
        this.executor = executor;
        this.executor.setStateMachine(this);
    }

    public List<GameStateConnection<C>> getConnections() {
        return data.getConnections();
    }

    public GameState<C> getStartState() {
        return data.getStartState();
    }

    public List<GameState<C>> getStates() {
        return data.getStates();
    }

    public void setStartState(GameState<C> startState) {
        data.setStartState(startState);
    }

    public void setStates(List<GameState<C>> states) {
        data.setStates(states);
    }

    public void setConnections(List<GameStateConnection<C>> GameStateConnections) {
        data.setConnections(GameStateConnections);
    }

    public List<GameStateConnection<C>> getConnectionsOf(GameState<C> GameState) {
        return getConnections().stream().filter(v -> v.getStartingState().equals(GameState)).collect(Collectors.toList());
    }

    public MachineRenderer<C> renderer() {
        if (renderer == null) renderer = new MachineRenderer<>(this);
        return renderer;
    }

    public void saveTo(String filename) {
        Serializer.save(data, filename);
    }

    public void loadFrom(String filename) {
        data = Deserializer.load(filename, data.getClass());
    }

    public Builder<C> builder() {
        if (builder == null) builder = new Builder<>(this);
        return builder;
    }

    public GameExecutor<C> executor() {
        if( executor == null) executor = new GameExecutor<>(this);
        return executor;
    }

    public void apply(IterationAction<C> action) {
        getStates().forEach(action::execute);
    }


}
