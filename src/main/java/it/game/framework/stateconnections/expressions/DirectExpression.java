package it.game.framework.stateconnections.expressions;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.stateconnections.interfaces.Expression;

public class DirectExpression implements Expression {

    @Override
    public boolean check(GameContext c) throws GameException {
        return true;
    }
}
