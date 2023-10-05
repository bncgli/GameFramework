package it.game.framework.states;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.executors.GameExecutor;
import it.game.framework.executors.interfaces.IGameExecutor;
import it.game.framework.statemachines.StateMachine;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutorGameState extends GameState {

    private IGameExecutor executor;

    public ExecutorGameState(IGameExecutor executor, StateMachine machine, GameContext context) {
        super("ExecutorGameState", "Executes a state machine with a relative context");
        this.executor = executor;
        executor.setStateMachine(machine);
        executor.setContext(context);
    }

    public ExecutorGameState(StateMachine machine, GameContext context) {
        super("ExecutorGameState", "Executes a state machine with a relative context");
        this.executor = new GameExecutor(
                true,
                true,
                null,
                machine,
                context
        );
    }

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
}
