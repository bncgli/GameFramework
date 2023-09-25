package it.game.framework.states.library.structures;

import it.game.framework.contexts.GameContext;
import it.game.framework.states.GameState;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GameStateCondition<C extends GameContext> implements Serializable {


    public interface Expression <C extends GameContext> extends Serializable{
        boolean check(GameState<C> self, C context);
    }

    private String expressionDescription;
    private Expression<C> expression;
    private GameState<C> resultState;

    public static <C extends GameContext> GameStateCondition<C> create(String expressionDescription, Expression<C> expression, GameState<C> resultState){
        return new GameStateCondition<>(expressionDescription, expression, resultState);
    }


    public GameStateCondition(Expression<C> expression, GameState<C> resultState) {
        this.expressionDescription = null;
        this.expression = expression;
        this.resultState = resultState;
    }

    public GameStateCondition(String expressionDescription, Expression<C> expression, GameState<C> resultState) {
        this.expressionDescription = expressionDescription;
        this.expression = expression;
        this.resultState = resultState;
    }

    public boolean getExpression(GameState<C> self, C context) {
        return expression.check(self, context);
    }

    @Override
    public String toString() {
        return "GameStateCondition{" +
                "Condition, resultState=" + resultState.getName() +
                '}';
    }
}
