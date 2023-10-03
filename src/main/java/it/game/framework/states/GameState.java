package it.game.framework.states;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


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

    public String ID() {
        return String.format("%s.%03d", name, id);
    }

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
