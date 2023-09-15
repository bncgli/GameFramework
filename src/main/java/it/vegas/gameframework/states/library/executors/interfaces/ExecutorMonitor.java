package it.vegas.gameframework.states.library.executors.interfaces;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;

public interface ExecutorMonitor {
    <C extends GameContext> void beforeLoop(GameState<C> currentState);

    <C extends GameContext> void beforeExecution(GameState<C> currentState);

    <C extends GameContext> void afterExecution(GameState<C> currentState);

    <C extends GameContext> void nextSelectedGamestate(GameState<C> currentState);

    <C extends GameContext> void afterLoop(GameState<C> currentState);

    <C extends GameContext> void nextSelectedGamestate(GameState<C> currentState, GameState<C> nextGameState);

    <C extends GameContext> void caughtException(GameState<C> currentState, Exception e);

}
