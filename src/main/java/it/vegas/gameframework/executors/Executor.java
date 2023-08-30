package it.vegas.gameframework.executors;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.enums.ActionReturn;
import it.vegas.gameframework.rules.GameRules;
import it.vegas.gameframework.states.GameState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
public class Executor<C extends GameContext, R extends GameRules> extends GameState<C, R> {

    private GameState<C,R> startingState;
    private GameState<C,R> currentState;

    @Override
    public ActionReturn execute() {
        currentState = startingState;
        while (currentState != null) {
            enterState();
            ActionReturn result = currentState.execute();
            if (result != ActionReturn.OK) {
                return result;
            }
            exitState();
            currentState = currentState.getNextGameState();
        }
        return ActionReturn.OK;
    }

    public void enterState() {
        System.out.printf("Starting: %s\n", currentState.getName());
    }

    public void exitState() {
        System.out.printf("Exiting: %s\n", currentState.getName());
    }

}
