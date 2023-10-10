package it.game.framework.executors.interfaces;

import it.game.framework.contexts.GameContext;
import it.game.framework.stateconnections.ExceptionStateConnection;
import it.game.framework.stateconnections.GameStateConnection;
import it.game.framework.states.GameState;

import java.io.Serializable;
import java.util.List;

public abstract class ExecutorCallback implements Serializable {
    public void beforeLoop(GameContext context) {
    }

    public void beforeExecution(GameState currentState, GameContext context) {
    }

    public void afterExecution(GameState currentState, GameContext context) {
    }

    public void nextSelectedGameState(GameState currentState, GameState nextGameState, GameContext context) {
    }

    public  void afterLoop(GameContext context) {
    }

    public void caughtException(GameState currentState, Exception e, GameContext context) {
    }

    public void connectionChoice(GameState currentState, List<GameStateConnection> choices, GameContext context) {
    }

    public void exceptionConnectionChoice(GameState currentGameState, List<ExceptionStateConnection> gameStateConnections, GameContext context) {
    }
}
