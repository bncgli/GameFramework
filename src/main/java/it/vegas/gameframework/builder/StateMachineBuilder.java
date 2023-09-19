package it.vegas.gameframework.builder;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.serializations.Serializer;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.interfaces.actions.GameStateAction;
import it.vegas.gameframework.states.library.structures.GameStateCondition;

import java.io.Serializable;
import java.util.List;

/**
 * The StateMachineBuilder class uses the Builder Design pattern
 * to simplify the creation of the state machine
 */
public class StateMachineBuilder {

    /**
     * Utility class it used only for create the root state of the
     * state machine it exists only for usability purpose
     *
     * @param <C> context class that must extend GameContext
     */
    public static class EmptyBuilder<C extends GameContext> implements Serializable {

        private final C context;

        public EmptyBuilder(C context) {
            this.context = context;
        }


        /**
         * Creates a new Builder class and stores a pre-made
         * GameState inside it.
         *
         * @param gameState The stored GameState
         * @return Returns a builder class to create the state machine
         */
        public Builder<C> newGameState(GameState<C> gameState) {
            Builder<C> child = new Builder<>(gameState, context, null);
            return child;
        }

        /**
         * Creates a new Builder class
         *
         * @return Returns a builder class to create the state machine
         */
        public Builder<C> newGameState() {
            return newGameState(new GameState<>());
        }

    }


    /**
     * The blocks of the builder class to create the state machine
     *
     * @param <C> the class of the context that has to extend GameContext
     */
    public static class Builder<C extends GameContext> implements Serializable{
        private GameState<C> gameState;
        private final Builder<C> parent;

        public Builder(GameState<C> gameState, C context, Builder<C> parent) {
            this.gameState = gameState;
            this.gameState.setContext(context);
            this.parent = parent;
        }

        /**
         * Sets the name of the GameState
         *
         * @param name The new name of the GameState
         * @return Returns self
         */
        public Builder<C> setName(String name) {
            this.gameState.setName(name);
            return this;
        }

        /**
         * Sets the description of the GameState
         *
         * @param description The new description of the GameState
         * @return Returns self
         */
        public Builder<C> setDescription(String description) {
            this.gameState.setDescription(description);
            return this;
        }

        /**
         * Sets the Context of the GameState,
         * in case there are more than one
         * context can be updated here
         *
         * @param context The new context of the GameState
         * @return Returns self
         */
        public Builder<C> setContext(C context) {
            this.gameState.setContext(context);
            return this;
        }

        /**
         * Sets the Action of the GameState
         *
         * @param action The new action of the GameState it can be also added as a lambda function with one parameter (the referring GameState) (self)-> code
         * @return Returns self
         */
        public Builder<C> setAction(GameStateAction<C> action) {
            this.gameState.setAction(action);
            return this;
        }

        /**
         * Sets the given GameState as the builded one;
         *
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
         *
         * @param nextGameStates A list of GameStateConditions
         * @return The built statemachine as the root node
         */
        public GameState<C> setBranchingStates(List<GameStateCondition<C>> nextGameStates) {
            this.gameState.setNextGameStates(nextGameStates);
            return this.build();
        }

        /**
         * Stores the conditions to new states or "branches"
         * as a varargs.
         *
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
         *
         * @param gameState The given GameState
         * @return returns self
         */
        public Builder<C> setNextGameState(GameState<C> gameState) {
            Builder<C> child = new Builder<>(gameState, this.gameState.getContext(), this);
            child.setGameState(gameState);
            this.gameState.setNextGameStates(
                    List.of(
                            new GameStateCondition<>(
                                    "",
                                    (s, context) -> true,
                                    child.gameState
                            )
                    )
            );
            return child;
        }

        /**
         * Adds a single GameStateCondition that is always true.
         * Useful for a sequence of states without branching
         *
         * @return returns self
         */
        public Builder<C> setNextGameState() {
            return setNextGameState(new GameState<>());
        }

        /**
         * Build the statemachine and returns the root of the state
         * machine
         *
         * @return The GameState that is at the root of the state machine
         */
        public GameState<C> build() {

            if (parent != null) {
                return parent.build();
            } else {
                return gameState;
            }
        }

    }

    /**
     * This method starts the process of building a state machine
     *
     * @param context The game context that will be passed to each element of the state machine
     * @param <C>     The class of the context it has to extends GameContext
     * @return Returns an EmptyBuilder this is utility class only for usability purpose
     */
    public static <C extends GameContext> EmptyBuilder<C> builder(C context) {
        return new EmptyBuilder<>(context);
    }

}
