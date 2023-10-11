package it.game.framework.stateconnections;

import it.game.framework.contexts.GameContext;
import it.game.framework.stateconnections.interfaces.Expression;
import it.game.framework.states.GameState;

public class ExceptionStateConnection extends GameStateConnection {

    int exceptionToCatch;
    Exception e;

    public ExceptionStateConnection(String expressionDescription, GameState startingState, int hash, GameState resultState) {
        super(expressionDescription, startingState, null, resultState);
        this.exceptionToCatch = hash;
    }

    public ExceptionStateConnection(String expressionDescription, GameState startingState, Exception exception, GameState resultState) {
        super(expressionDescription, startingState, null, resultState);
        this.e = exception;
        this.exceptionToCatch = this.e.hashCode();
    }

    @Override
    public Expression getExpression() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExpression(Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkExpression(GameContext c) {
        return false;
    }

    public boolean checkExpression(Exception c) {
        if (e == null) return c.hashCode() == exceptionToCatch;
        return c.hashCode() == e.hashCode();
    }
}
