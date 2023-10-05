package it.game.framework.stateconnections;

import it.game.framework.contexts.GameContext;
import it.game.framework.stateconnections.interfaces.Expression;
import it.game.framework.states.GameState;

public class ExceptionStateConnection extends GameStateConnection{
    
    Exception exceptionToCatch;
    
    public ExceptionStateConnection(String expressionDescription, GameState startingState, Exception e, GameState resultState) {
        super(expressionDescription, startingState, null, resultState);
        this.exceptionToCatch = e;
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
    public boolean checkExpression(GameContext c) throws Exception {
        return false;
    }
    
    public boolean checkExpression(Exception c) throws Exception {
        return c.equals(exceptionToCatch);
    }
}
