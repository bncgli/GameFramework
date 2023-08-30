package it.vegas.gameframework.states;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.enums.ActionReturn;
import it.vegas.gameframework.rules.GameRules;
import it.vegas.gameframework.states.interfaces.actions.GameStateAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class GameState<C extends GameContext, R extends GameRules> {

    private String name = "GameState";
    private String description = "";
    private C context;
    private R rules;
    private GameState<C, R> nextGameState;
    private GameStateAction<C, R> action = (self, context, rules) -> {
        System.out.println("Unimplemented");
        return ActionReturn.OK;
    };

    public ActionReturn execute() {
        return action.execute(this, context, rules);
    }

    @Override
    public String toString() {
        return "GameState{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", context=" + context +
                ", rules=" + rules +
                ", nextGameState=" + (nextGameState != null ? nextGameState.getName() : "null") +
                ", action=" + action +
                '}';
    }
}
