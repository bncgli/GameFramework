package it.vegas.gameframework.states;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.rules.GameRules;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CompositeGameState<C extends GameContext, R extends GameRules> extends GameState<C,R>{

    private int nextGameStateIndex = 0;
    @Builder.Default
    private List<GameState<C,R>> nextGameStateList = new ArrayList<>();

    @Override
    public GameState<C, R> getNextGameState() {
        if(nextGameStateIndex < 0 || nextGameStateIndex >= nextGameStateList.size()){
            System.out.println("Index out of bound");
            return null;
        }
        if(nextGameStateList.isEmpty()){
            return null;
        }
        return nextGameStateList.get(nextGameStateIndex);
    }
}
