package it.vegas.gameframework.navigators;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.library.structures.GameStateCondition;

public class Navigator {

    public static <C extends GameContext> void printMachine(GameState<C> start) {
        printMachine(start, "  ");
    }

    private static <C extends GameContext> void printMachine(GameState<C> start, String indent) {
        System.out.println(indent+start.toString());
        for (GameStateCondition<C> i : start.getNextGameStates()) {

            Navigator.printMachine(i.getResultState()," "+indent.replace("└─","  ")+"└─ ");
        }
    }

}
