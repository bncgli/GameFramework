package it.game.framework.stateconnections;

import it.game.framework.contexts.GameContext;
import it.game.framework.stateconnections.interfaces.Expression;
import it.game.framework.states.GameState;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class contains the connections from a GameState to another
 * with the relative condition(Expression) to confirm this action
 */
@Getter
@Setter
public class GameStateConnection implements Serializable {

    private String expressionDescription;
    private Expression expression;
    private GameState startingState;
    private GameState resultState;

    public GameStateConnection(String expressionDescription, GameState startingState, Expression expression, GameState resultState) {
        this.expressionDescription = expressionDescription;
        this.startingState = startingState;
        this.expression = expression;
        this.resultState = resultState;
    }

    /**
     * Calculate and returns the result of the expression
     *
     * @param c The GameContext
     * @return True or false depending of the expression's formula
     * @throws Exception
     */
    public boolean checkExpression(GameContext c) throws Exception {
        return expression.check(c);
    }

    @Override
    public String toString() {
        return String.format("[Condition: %s -> %s%s]",
                startingState == null ? "GLOBAL" : startingState.getName(),
                expressionDescription == null || expressionDescription.isEmpty() ? "" : expressionDescription + " -> ",
                resultState == null ? "EXIT" : resultState.getName()
        );
    }

}
