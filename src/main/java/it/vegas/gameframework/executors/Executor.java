package it.vegas.gameframework.executors;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.enums.ActionReturn;
import it.vegas.gameframework.monitors.Monitor;
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
    private Monitor monitor;

    public void setStartingState(GameState<C, R> startingState) {
        this.startingState = startingState;
        monitor.setStateMachine(startingState);
    }

    @Override
    public ActionReturn execute() {
        currentState = startingState;
        while (currentState != null) {
            monitor.enterState(currentState);
            ActionReturn result = currentState.execute();
            if (result != ActionReturn.OK) {
                System.out.println(currentState.getName()+" returned: "+ result + " and exited");
                return result;
            }
            monitor.exitState(currentState);
            currentState = currentState.getNextGameState();
        }
        return ActionReturn.OK;
    }

}
