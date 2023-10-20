package it.game.framework.states;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.executors.GameExecutor;
import it.game.framework.executors.interfaces.ExecutorCallback;
import it.game.framework.executors.interfaces.IGameExecutor;
import it.game.framework.statemachines.StateMachine;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This special GameState contains a IGameExecutor
 * the executes a different StateMachine with the relative context
 */
public class ExecutorGameState extends GameState {

    private final IGameExecutor executor;

    public ExecutorGameState(StateMachine machine, GameContext context) {
        this(machine, context, null);
    }

    public ExecutorGameState(StateMachine machine, GameContext context, List<ExecutorCallback> callbacks) {
        super("ExecutorGameState", "Executes a state machine with a relative context");
        this.executor = new GameExecutor(true, true, null, machine, context, callbacks);
    }

    /**
     * Executes the StateMachine and context passed into this GameState
     * into the IGameExecutor
     * @param c The GameContext
     * @throws GameException
     */
    @Override
    public void execute(GameContext c) throws GameException {
        executor.execute();
    }

    public GameState getCurrentGameState() {
        return executor.getCurrentGameState();
    }

    public StateMachine getStateMachine() {
        return executor.getStateMachine();
    }

    public GameContext getContext() {
        return executor.getContext();
    }

    public void setCurrentGameState(GameState currentGameState) {
        executor.setCurrentGameState(currentGameState);
    }

    public void setStateMachine(StateMachine stateMachine) {
        executor.setStateMachine(stateMachine);
    }

    public void setContext(GameContext context) {
        executor.setContext(context);
    }

    public List<ExecutorCallback> getCallbacks() {
        return executor.getCallbacks();
    }

    public void setCallbacks(List<ExecutorCallback> callback) {
        executor.setCallbacks(callback);
    }
}
