package it.game.framework.executors.interfaces;

import it.game.framework.exceptions.GameException;
import it.game.framework.states.GameState;

public interface IGameExecutor {
    void execute();

    void begin() throws GameException;

    void process() throws Exception;

    void end();

    GameState getNextGameState() throws Exception;

    GameState getNextExceptionGameState(Exception e) throws Exception;
}
