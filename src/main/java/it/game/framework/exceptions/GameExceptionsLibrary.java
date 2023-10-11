package it.game.framework.exceptions;

import lombok.Getter;

@Getter
public enum GameExceptionsLibrary {


    //Building exceptions
    MACHINE_BUILDING_EXCEPTION(9100, "Machine building exception"),
    STARTING_STATE_IS_NOT_IN_MACHINE_STATES(9101, "Starting state is not in the machine's states list"),
    CONNECTION_STARTINGSTATE_IS_NULL(9102, "Connection's starting state missing"),
    CONNECTION_STATE_IS_NOT_IN_MACHINE_STATES(9103, "States in connection are not contained in the machine's states list"),
    DIRECT_CONNECTION_IS_NOT_LAST(9104, "A connection that returns always TRUE is not last, connections following this one are not reachable"),
    DIRECT_CONNECTION_IN_GLOBALS(9105, "A connection that returns always TRUE is in the global connections, connections following this one are not reachable"),

    //Execution exceptions
    EXECUTION_EXCEPTION(9200, "Machine execution exception"),
    STATEMACHINE_IS_NULL(9201, "The state machine reference inside the executor is null"),
    CONTEXT_IS_NULL(9202, "The context reference inside the executor is null"),
    STARTING_STATE_IS_NULL(9203, "Machine's starting state is null"),
    EXECUTOR_MONITOR_IS_NULL(9204, "The executor monitor is null"),
    GAMEEXECUTOR_IS_NULL(9205, "The GameExecutor instance inside the ThreadedExecutor is null"),

    //GameContext Exceptions
    CONTEXT_EXCEPTION(9300, "GameContext exception"),
    CONTEXT_CASTING_EXCEPTION(9301, "Object cannot be casted in the specified class")
    ;


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
