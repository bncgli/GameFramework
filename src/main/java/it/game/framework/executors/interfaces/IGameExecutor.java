package it.game.framework.executors.interfaces;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;

public interface IGameExecutor {
    void execute();

    GameState getCurrentGameState();

    StateMachine getStateMachine();

    GameContext getContext();

    void setCurrentGameState(GameState currentGameState);

    void setStateMachine(StateMachine stateMachine);

    void setContext(GameContext context);

    ExecutorCallback getCallback();

    void setCallback(ExecutorCallback callback);
}
