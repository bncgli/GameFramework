package it.vegas.gameframework.builder;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.interfaces.actions.GameStateAction;
import it.vegas.gameframework.states.library.structures.GameStateCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * The StateMachineBuilder class uses the Builder Design pattern
 * to simplify the creation of the state machine
 */
public class StateMachineBuilder {

    /**
     * Utility class it used only for create the root state of the
     * state machine it exists only for usability purpose
     * @param <C> context class that must extend GameContext
     */
    public static class EmptyBuilder<C extends GameContext> {

        private final C context;

        public EmptyBuilder(C context) {
            this.context = context;
        }


        /**
         * Creates a new Builder class and stores a pre-made
         * GameState inside it.
         * @param gameState The stored GameState
         * @return Returns a builder class to create the state machine
         */
        public Builder<C> newGameState(GameState<C> gameState) {
            Builder<C> child = new Builder<>(context, null);
            child.setGameState(gameState);
            return child;
        }

        /**
         * Creates a new Builder class
         * @return Returns a builder class to create the state machine
         */
        public Builder<C> newGameState() {
            return newGameState(null);
        }

    }


    /**
     * The blocks of the builder class to create the state machine
     * @param <C> the class of the context that has to extend GameContext
     */
    public static class Builder<C extends GameContext> {
        private String name = "GameState";
        private String description = "";
        private C context;
        private List<GameStateCondition<C>> nextGameStates = new ArrayList<>();
        private GameStateAction<C> action = (self) -> System.out.println("Unimplemented");
        private GameState<C> gameState;
        private final Builder<C> parent;

        public Builder(C context, Builder<C> parent) {
            this.context = context;
            this.parent = parent;
        }

        /**
         * Sets the name of the GameState
         * @param name The new name of the GameState
         * @return Returns self
         */
        public Builder<C> setName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the description of the GameState
         * @param description The new description of the GameState
         * @return Returns self
         */
        public Builder<C> setDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the Context of the GameState,
         * in case there are more than one
         * context can be updated here
         * @param context The new context of the GameState
         * @return Returns self
         */
        public Builder<C> setContext(C context) {
            this.context = context;
            return this;
        }

        /**
         * Sets the Action of the GameState
         * @param action The new action of the GameState it can be also added as a lambda function with one parameter (the referring GameState) (self)-> code
         * @return Returns self
         */
        public Builder<C> setAction(GameStateAction<C> action) {
            this.action = action;
            return this;
        }

        /**
         * Sets the given GameState as the builded one;
         * @param state The new GameState
         * @return Returns self
         */
        private Builder<C> setGameState(GameState<C> state) {
            this.gameState = state;
            return this;
        }

        /**
         * Stores the conditions to new states or "branches"
         * as a list.
         * @param nextGameStates A list of GameStateConditions
         * @return The built statemachine as the root node
         */
        public GameState<C> setBranchingStates(List<GameStateCondition<C>> nextGameStates) {
            this.nextGameStates = nextGameStates;
            return this.build();
        }

        /**
         * Stores the conditions to new states or "branches"
         * as a varargs.
         * @param nextGameStates A list of GameStateConditions
         * @return The built statemachine as the root node
         */
        @SafeVarargs
        public final GameState<C> setBranchingStates(GameStateCondition<C>... nextGameStates) {
            return setBranchingStates(List.of(nextGameStates));
        }

        /**
         * Adds a single GameStateCondition with a given gameState
         * that is always true. Useful for a sequence of states
         * without branching
         * @param gameState The given GameState
         * @return returns self
         */
        public Builder<C> setNextGameState(GameState<C> gameState) {
            Builder<C> child = new Builder<>(context, this);
            child.setGameState(gameState);
            nextGameStates=List.of(
                    new GameStateCondition<>(
                            "",
                            (s) -> true,
                            child.compile()
                    )
            );
            return child;
        }

        /**
         * Adds a single GameStateCondition that is always true.
         * Useful for a sequence of states without branching
         * @return returns self
         */
        public Builder<C> setNextGameState() {
            return setNextGameState(null);
        }

        /**
         * Modify or create and populate the GameState
         * @return The compiled GameState
         */
        private GameState<C> compile() {
            if (gameState == null) {
                gameState = new GameState<>();
            }

            gameState.setName(name);
            gameState.setDescription(description);
            gameState.setNextGameStates(nextGameStates);
            gameState.setAction(action);
            gameState.setContext(context);

            return gameState;
        }


        /**
         * Build the statemachine and returns the root of the state
         * machine
         * @return The GameState that is at the root of the state machine
         */
        public GameState<C> build() {
            gameState = compile();

            if (parent != null) {
                return parent.build();
            } else {
                return gameState;
            }
        }


    }

    /**
     * This method starts the process of building a state machine
     * @param context The game context that will be passed to each element of the state machine
     * @param <C> The class of the context it has to extends GameContext
     * @return Returns an EmptyBuilder this is utility class only for usability purpose
     */
    public static <C extends GameContext> EmptyBuilder<C> builder(C context) {
        return new EmptyBuilder<>(context);
    }

}
