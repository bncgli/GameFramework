package it.game.framework.stateconnections;

import it.game.framework.contexts.GameContext;
import it.game.framework.stateconnections.interfaces.Expression;
import it.game.framework.states.GameState;

/**
 * This GameStateConnection is a special one, this type of connections
 * are checked only when an Exception occurs inside the main loop of a GameExecutor
 * these are used to handle exceptions and move the StateMachine to a state where the
 * exception can be handled or close pending operations.
 */
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

    /**
     * This method returns an UnsupportedOperationException();
     * This override is only to impede to mishandle this GameStateConnection
     */
    @Override
    public Expression getExpression() {
        throw new UnsupportedOperationException();
    }

    /**
     * This method returns an UnsupportedOperationException();
     * This override is only to impede to mishandle this GameStateConnection
     */
    @Override
    public void setExpression(Expression expression) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is overridden to return always false, this type of
     * connection is only called when en exception occurs inside the
     * main loop of a GameExecutor.
     * @param c The GameContext
     * @return Always returns False
     */
    @Override
    public boolean checkExpression(GameContext c) {
        return false;
    }

    /**
     * This class checks the caught exception passed as argument and
     * returns true if the exception corresponds to the one we want to catch
     * @param c The exception we want to catch
     * @return True if the exception passed as argument is the one we want to catch
     */
    public boolean checkExpression(Exception c) {
        if (e == null) return c.hashCode() == exceptionToCatch;
        return c.hashCode() == e.hashCode();
    }
}
