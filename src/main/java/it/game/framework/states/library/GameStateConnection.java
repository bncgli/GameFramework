package it.game.framework.states.library;

import it.game.framework.contexts.GameContext;
import it.game.framework.states.GameState;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GameStateConnection<C extends GameContext> implements Serializable {


    public interface Expression<C extends GameContext> extends Serializable {
        boolean check(C context);
    }

    private String expressionDescription;
    private Expression<C> expression;
    private GameState<C> startingState;
    private GameState<C> resultState;

    public static <C extends GameContext> GameStateConnection<C> create(String expressionDescription, GameState<C> startingState, Expression<C> expression, GameState<C> resultState) {
        return new GameStateConnection<>(expressionDescription == null ? "No description" : expressionDescription, startingState, expression, resultState);
    }

    public static <C extends GameContext> GameStateConnection<C> createDirect(GameState<C> startingState, GameState<C> resultState) {
        return new GameStateConnection<>(null, startingState, (c) -> true, resultState);
    }

    public GameStateConnection(String expressionDescription, GameState<C> startingState, Expression<C> expression, GameState<C> resultState) {
        this.expressionDescription = expressionDescription;
        this.startingState = startingState;
        this.expression = expression;
        this.resultState = resultState;
    }

    public boolean getExpression(C context) {
        return expression.check(context);
    }

    @Override
    public String toString() {
        return String.format("[Condition: %s -> %s%s]",
                startingState.getName(),
                expressionDescription==null?"":expressionDescription+" -> ",
                resultState.getName()
        );
    }
}
