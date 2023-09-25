package it.game.framework.states;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.states.interfaces.actions.GameStateAction;
import it.game.framework.states.library.structures.GameStateCondition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Slf4j
public class GameState<C extends GameContext> implements Serializable {

    protected String name;
    protected String description;
    protected C context;
    protected List<GameStateCondition<C>> nextGameStates;
    protected GameStateAction<C> action;

    public GameState() {
        this.name = "GameState";
        this.description = "";
        this.context = null;
        this.nextGameStates = new ArrayList<>();
        this.action = (self, context) -> log.warn("Unimplemented action in {}", self);
    }

    public GameState(String name) {
        this();
        this.name = name;
    }

    public GameState(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    public GameState(String name, String description, C context, List<GameStateCondition<C>> nextGameStates, GameStateAction<C> action) {
        this.name = name;
        this.description = description;
        this.context = context;
        this.nextGameStates = nextGameStates;
        this.action = action;
    }

    public void execute() throws GameException {
        action.execute(this, context);
    }

    /**
     * Iters all the gamestateconditions and return the game state of
     * the FIRST game state condition returning TRUE
     * @return The gameState with the condition that returned true
     */
    public GameState<C> getNextGameState() {
        List<GameStateCondition<C>> gameStateConditions = nextGameStates;
        for (GameStateCondition<C> c : gameStateConditions) {
            if (c.getExpression(this, context)) {
                return c.getResultState();
            }
        }
        return null;
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

}
