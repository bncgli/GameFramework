package it.vegas.gameframework.rules;


import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.enums.ActionReturn;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.interfaces.actions.GameStateAction;
import org.springframework.stereotype.Component;

@Component
public class GameRules {

    public static class testRule<C extends GameContext,R extends GameRules> implements GameStateAction<C,R>{

        @Override
        public ActionReturn execute(GameState<C, R> self, C context, R rules) {
            return null;
        }
    }

}
