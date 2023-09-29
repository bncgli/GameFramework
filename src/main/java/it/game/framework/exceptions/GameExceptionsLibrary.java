package it.game.framework.exceptions;

import lombok.Getter;

@Getter
public enum GameExceptionsLibrary {

    //Building exceptions
    MACHINE_BUILDING_EXCEPTION(100, "Machine building exception"),
    STARTING_STATE_IS_NOT_IN_MACHINE_STATES(101, "Starting state is not in the machine's states list"),
    CONNECTION_STARTINGSTATE_IS_NULL(102, "Connection's starting state missing"),
    CONNECTION_STATE_IS_NOT_IN_MACHINE_STATES(103, "States in connection are not contained in the machine's states list"),
    DIRECT_EXPRESSION_IS_NOT_LAST(104, "The connection with a direct expression is not last, expression following this are not reachable"),

    //Execution exceptions
    EXECUTION_EXCEPTION(200, "Machine execution exception"),
    STATEMACHINE_IS_NULL(201, "The state machine reference inside the executor is null"),
    STARTING_STATE_IS_NULL(202, "Machine's starting state is null");


    private final int id;
    private final String message;

    GameExceptionsLibrary(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public GameExceptionsLibrary getByID(int id) {
        for (GameExceptionsLibrary e : GameExceptionsLibrary.values()) {
            if (e.id == id) return e;
        }
        return null;
    }
}
