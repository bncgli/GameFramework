package it.vegas.gameframework.monitors;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.rules.GameRules;
import it.vegas.gameframework.states.GameState;

public interface Monitor {

    default <C extends GameContext, R extends GameRules> void setStateMachine(GameState<C,R> startingState){
        GameState<C,R> currentState = startingState;
        while (currentState != null){
            System.out.println(currentState.toString());
            currentState = currentState.getNextGameState();
        }
    }

    default <C extends GameContext, R extends GameRules> void enterState(GameState<C,R> state) {
        System.out.printf("Starting: %s\n", state.getName());
    }

    default <C extends GameContext, R extends GameRules> void exitState(GameState<C,R> state) {
        System.out.printf("Exiting: %s\n", state.getName());
    }

}
