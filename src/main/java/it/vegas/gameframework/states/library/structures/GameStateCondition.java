package it.vegas.gameframework.states.library.structures;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class GameStateCondition<C extends GameContext> {


    public interface Expression <C extends GameContext>{
        boolean check(GameState<C> self);
    }

    private String expressionDescription;
    private Expression<C> expression;
    private GameState<C> resultState;


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

    public boolean getExpression(GameState<C> self) {
        return expression.check(self);
    }

    @Override
    public String toString() {
        return "GameStateCondition{" +
                "Condition, resultState=" + resultState.getName() +
                '}';
    }
}
