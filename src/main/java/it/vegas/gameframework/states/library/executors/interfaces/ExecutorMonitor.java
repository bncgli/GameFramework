package it.vegas.gameframework.states.library.executors.interfaces;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;

import java.io.Serializable;

public interface ExecutorMonitor extends Serializable {
    void beforeLoop();

    <C extends GameContext> void beforeExecution(GameState<C> currentState);

    <C extends GameContext> void afterExecution(GameState<C> currentState);

    <C extends GameContext> void nextSelectedGameState(GameState<C> currentState, GameState<C> nextGameState);

    void afterLoop();

    <C extends GameContext> void caughtException(GameState<C> currentState, Exception e);

}
