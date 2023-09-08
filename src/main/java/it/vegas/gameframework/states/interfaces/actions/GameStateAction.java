package it.vegas.gameframework.states.interfaces.actions;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;

public interface GameStateAction <C extends GameContext>{

    void execute(GameState<C> self);
}
