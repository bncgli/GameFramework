package it.vegas.gameframework.states;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.interfaces.actions.GameStateAction;
import it.vegas.gameframework.states.library.structures.GameStateCondition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Slf4j
public class GameState<C extends GameContext> {

    protected String name = "GameState";
    protected String description = "";
    protected C context;
    protected List<GameStateCondition<C>> nextGameStates;
    protected GameStateAction<C> action;

    public GameState() {
        this.name = "GameState";
        this.description = "";
        this.context = null;
        this.nextGameStates = new ArrayList<>();
        this.action = (self) -> log.warn("Unimplemented action in {}", self);;
    }

    public GameState(String name, String description, C context, List<GameStateCondition<C>> nextGameStates, GameStateAction<C> action) {
        this.name = name;
        this.description = description;
        this.context = context;
        this.nextGameStates = nextGameStates;
        this.action = action;
    }

    public void execute() {
        action.execute(this);
    }

    @Override
    public String toString() {
        return "GameState{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", context=" + context +
                ", nextGameState=" + (nextGameStates.stream().map(GameStateCondition::toString).toList()) +
                ", action=" + action +
                '}';
    }

    public List<GameState<C>> getMachineAsList() {
        return getMachineAsList(new ArrayList<>());
    }

    protected List<GameState<C>> getMachineAsList(List<GameState<C>> visited) {
        visited.add(this);
        for (GameStateCondition<C> c : nextGameStates) {
            if (!visited.contains(c.getResultState())) {
                visited.addAll(c.getResultState().getMachineAsList(visited));
            }
        }
        return visited;
    }
}
