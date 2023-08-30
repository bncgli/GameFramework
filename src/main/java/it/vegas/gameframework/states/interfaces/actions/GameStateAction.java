package it.vegas.gameframework.states.interfaces.actions;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.enums.ActionReturn;
import it.vegas.gameframework.rules.GameRules;
import it.vegas.gameframework.states.GameState;

public interface GameStateAction <C extends GameContext, R extends GameRules>{

    ActionReturn execute(GameState<C,R> self, C context, R rules);
}
