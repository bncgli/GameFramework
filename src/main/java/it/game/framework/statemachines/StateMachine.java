package it.game.framework.statemachines;

import it.game.framework.contexts.GameContext;
import it.game.framework.renderers.MachineRenderer;
import it.game.framework.serializations.Deserializer;
import it.game.framework.serializations.Serializer;
import it.game.framework.statemachines.interfaces.IterationAction;
import it.game.framework.states.GameState;
import it.game.framework.states.library.executors.GameExecutor;
import it.game.framework.states.library.structures.GameStateCondition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class StateMachine<C extends GameContext> {

    private MachineRenderer.GraphSpecifics graphSpecs;

    private GameState<C> stateTree;
    private GameExecutor<C> executor;

    public StateMachine(String filename) {
        stateTree = new GameState<>();
        stateTree = Deserializer.load(filename, stateTree.getClass());
        executor = new GameExecutor<>();
        executor.setStartingState(stateTree);
        graphSpecs = MachineRenderer.GraphSpecifics.builder().build();
    }

    public StateMachine(GameState<C> stateTree, GameExecutor<C> executor) {
        this.stateTree = stateTree;
        this.executor = executor;
        executor.setStartingState(stateTree);
        graphSpecs = MachineRenderer.GraphSpecifics.builder().build();
    }

    public StateMachine(GameState<C> stateTree) {
        this(stateTree, new GameExecutor<>(stateTree));
    }

    public static <C extends GameContext> StateMachine<C> create(GameState<C> startingState) {
        return new StateMachine<>(startingState);
    }

    public void renderGraph(String filename) {
        MachineRenderer.renderGraph(stateTree, filename, graphSpecs);
    }

    public void saveTo(String filename) {
        Serializer.save(stateTree, filename);
    }

    public void loadFrom(String filename) {
        stateTree = Deserializer.load(filename, stateTree.getClass());
    }

    public C getContext() {
        return stateTree.getContext();
    }

    public GameState<C> getCurrentState() {
        if (executor == null) return null;
        return executor.getCurrentState();
    }

    public void setCurrentState(GameState<C> currentState) {
        if (executor == null) return;
        executor.setCurrentState(currentState);
    }

    public void execute() {
        if (executor == null) {
            executor = GameExecutor.execute(stateTree);
            return;
        }
        executor.execute();
    }

    public void execute(GameExecutor<C> executor) {
        this.executor = executor;
        this.executor.setStartingState(stateTree);
        this.executor.execute();
    }

    public Set<GameState<C>> getGameStateSet() {
        Set<GameState<C>> visited = new HashSet<>();
        iterMachine(stateTree, visited, null);
        return visited;
    }

    public void apply(IterationAction<C> action) {
        iterMachine(stateTree, new LinkedList<>(), action);
    }

    public void applyTo(GameState<C> target, IterationAction<C> action) {
        iterMachine(stateTree, new LinkedList<>(), (s) -> {
            if (s.equals(target)) {
                action.execute(s);
            }
        });
    }

    private void iterMachine(GameState<C> start, Collection<GameState<C>> visited, IterationAction<C> action) {
        if (!visited.contains(start)) {
            visited.add(start);
            if (action != null) action.execute(start);
        } else {
            return;
        }

        for (GameStateCondition<C> i : start.getNextGameStates()) {
            iterMachine(i.getResultState(), visited, action);
        }
    }


}
