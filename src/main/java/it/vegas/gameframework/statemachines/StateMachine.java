package it.vegas.gameframework.statemachines;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.renderers.MachineRenderer;
import it.vegas.gameframework.serializations.Deserializer;
import it.vegas.gameframework.serializations.Serializer;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.library.executors.GameExecutor;
import it.vegas.gameframework.states.library.structures.GameStateCondition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class StateMachine<C extends GameContext> {

    /**
     * Interface for the creation of lambdas that are executes
     * in during th iteration of gamestates in the statemachine
     *
     * @param <C> context class that must extend GameContext
     */
    public interface IterationAction<C extends GameContext> extends Serializable {

        /**
         * Interface for Lambdas creation executed in
         * GameStates during the iteration of the statetree
         *
         * @param self the reference to the gameState where the lambda is executed
         */
        void execute(GameState<C> self);
    }

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
