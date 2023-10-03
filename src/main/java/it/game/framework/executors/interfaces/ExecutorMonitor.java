package it.game.framework.executors.interfaces;

import it.game.framework.contexts.GameContext;
import it.game.framework.states.GameState;

import java.io.Serializable;

public interface ExecutorMonitor extends Serializable {
    void beforeLoop();

    void beforeExecution(GameState currentState);

    void afterExecution(GameState currentState);

    void nextSelectedGameState(GameState currentState, GameState nextGameState);

    void afterLoop();

    void caughtException(GameState currentState, Exception e);

}
