package it.game.framework.stateconnections.interfaces;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;

import java.io.Serializable;

/**
 * This interface is used to create comparing expressions
 * or lambdas for GameStateConditions.
 */
public interface Expression extends Serializable {

    /**
     * Based of the data from the GameContext returns
     * true or false
     * @param c The GameContext of the game
     * @return True or false based on the expression/lambda programmed inside
     * @throws Exception
     */
    boolean check(GameContext c) throws Exception;
}
