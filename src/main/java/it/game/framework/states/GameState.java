package it.game.framework.states;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


/**
 * The GameState of the StateMachine, this abstract class contains
 * an abstract action that can be implemented as desired. This action
 * is executed during the execution of the StateMachine by an executor.
 */
@Getter
@Setter
@Slf4j
public abstract class GameState implements Serializable {

    public static int globalId = 0;

    protected int id;
    protected String name;
    protected String description;

    public GameState(String name, String description) {
        this.id = globalId++;
        this.name = name;
    }

    public GameState(String name) {
        this(name, "");
    }

    public GameState() {
        this("");
        this.name = this.getClass().getSimpleName();
    }

    /**
     * The unique name of the GameState, created for identification purpose
     * @return The ID of the class as a String formatted as "gamestate_name.id"(e.g. MyGameState.001)
     */
    public String ID() {
        return String.format("%s.%03d", name, id);
    }

    /**
     * The action that will be executed during the execution of the state into the StateMachine
     * @param c The GameContext
     * @throws GameException
     */
    public abstract void execute(GameContext c) throws GameException;

    @Override
    public String toString() {
        return "GameState<C>{" +
                "name='" + name + '\'' +
                "description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState GameState = (GameState) o;
        return this.ID().equals(GameState.ID());
    }

}
