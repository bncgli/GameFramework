package it.game.framework.stateconnections.interfaces;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;

import java.io.Serializable;

public interface Expression extends Serializable {


    boolean check(GameContext c) throws Exception;
}
