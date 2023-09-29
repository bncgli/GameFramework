package it.game.framework.executors.interfaces;

import it.game.framework.contexts.GameContext;
import it.game.framework.states.GameState;

import java.io.Serializable;

public interface ExecutorMonitor<C extends GameContext<C>> extends Serializable {
    void beforeLoop();

    void beforeExecution(GameState<C> currentState);

    void afterExecution(GameState<C> currentState);

    void nextSelectedGameState(GameState<C> currentState, GameState<C> nextGameState);

    void afterLoop();

    void caughtException(GameState<C> currentState, Exception e);

}
