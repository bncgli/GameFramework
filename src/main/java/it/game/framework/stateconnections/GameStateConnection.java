package it.game.framework.stateconnections;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.stateconnections.expressions.DirectExpression;
import it.game.framework.stateconnections.interfaces.Expression;
import it.game.framework.states.GameState;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GameStateConnection implements Serializable {


    private String expressionDescription;
    private Expression expression;
    private GameState startingState;
    private GameState resultState;

    public static GameStateConnection create(String expressionDescription, GameState startingState, Expression expression, GameState resultState) {
        return new GameStateConnection(expressionDescription == null ? "No description" : expressionDescription, startingState, expression, resultState);
    }

    public static GameStateConnection createDirect(GameState startingState, GameState resultState) {
        return new GameStateConnection(null, startingState, new DirectExpression(), resultState);
    }

    public GameStateConnection(String expressionDescription, GameState startingState, Expression expression, GameState resultState) {
        this.expressionDescription = expressionDescription;
        this.startingState = startingState;
        this.expression = expression;
        this.resultState = resultState;
    }

    public boolean checkExpression(GameContext c) throws Exception {
        return expression.check(c);
    }

    @Override
    public String toString() {
        return String.format("[Condition: %s -> %s%s]",
                startingState.getName(),
                expressionDescription == null ? "" : expressionDescription + " -> ",
                resultState.getName()
        );
    }
}
