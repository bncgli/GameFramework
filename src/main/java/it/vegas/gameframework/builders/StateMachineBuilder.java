package it.vegas.gameframework.builders;


import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.rules.GameRules;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.interfaces.actions.GameStateAction;

public class StateMachineBuilder {

    public static class StateMachine<C extends GameContext, R extends GameRules> {
        private String name = "";
        private String description = "";
        private C context;
        private R rules;
        private GameState<C,R> nextGameState;
        private GameStateAction<C, R> action;
        private GameState<C, R> gameState;
        private StateMachine<C,R> parent;

        public StateMachine(C context, R rules) {
            this.name = "start";
            this.description = null;
            this.context = context;
            this.rules = rules;
            this.nextGameState = null;
            this.action = null;
            this.gameState = null;
            this.parent = null;
        }

        public StateMachine(C context, R rules, StateMachine<C,R> parent) {
            this.name = null;
            this.description = null;
            this.context = context;
            this.rules = rules;
            this.nextGameState = null;
            this.action = null;
            this.gameState = null;
            this.parent = parent;
        }

        public StateMachine<C, R> setName(String name) {
            this.name = name;
            return this;
        }

        public StateMachine<C, R> setDescription(String description) {
            this.description = description;
            return this;
        }

        public StateMachine<C, R> setAction(GameStateAction<C, R> action) {
            this.action = action;
            return this;
        }

        public void setGameState(GameState<C, R> gameState) {
            this.gameState = gameState;
        }

        public StateMachine<C, R> goToNewState() {
            StateMachine<C, R> child = new StateMachine<>(context, rules, this);
            return child;
        }

        public StateMachine<C, R> goToState(GameState<C, R> gameState) {
            StateMachine<C, R> child = new StateMachine<>(context, rules, this);
            child.setGameState(gameState);
            return child;
        }

        public StateMachine<C,R> goToNewStateAndAction(GameStateAction<C,R> action){
            StateMachine<C, R> child = new StateMachine<>(context, rules,this);
            child.setAction(action);
            return child;
        }

        public StateMachine<C,R> goToStateAndAction(GameState<C,R> gameState,GameStateAction<C,R> action){
            StateMachine<C, R> child = new StateMachine<>(context, rules,this);
            child.setGameState(gameState);
            child.setAction(action);
            return child;
        }

        public GameState<C, R> build() {
            if (gameState == null){
                gameState = new GameState<>();
            }

            if (this.name != null) gameState.setName(this.name);
            if(this.description != null) gameState.setDescription(this.description);
            gameState.setContext(this.context);
            gameState.setRules(this.rules);
            gameState.setNextGameState(this.nextGameState);
            if(this.action != null) gameState.setAction(this.action);

            if(parent != null) {
                parent.nextGameState = gameState;
                return parent.build();
            }else{
                return gameState;
            }
        }
    }
    public static class EmptyStateMachine<C extends GameContext, R extends GameRules> {
        private final C context;
        private final R rules;

        public EmptyStateMachine(C context, R rules) {
            this.context = context;
            this.rules = rules;
        }

        public StateMachine<C, R> newState() {
            return new StateMachine<>(context, rules);
        }

        public StateMachine<C, R> newState(GameState<C, R> gameState) {
            StateMachine<C, R> child = new StateMachine<>(context, rules);
            child.setGameState(gameState);
            return child;
        }

        public StateMachine<C,R> newState(GameState<C,R> gameState,GameStateAction<C,R> action){
            StateMachine<C, R> child = new StateMachine<>(context, rules);
            child.setGameState(gameState);
            child.setAction(action);
            return child;
        }
    }
    public static <C extends GameContext, R extends GameRules> EmptyStateMachine<C, R> builder(C context, R rules) {
        return new EmptyStateMachine<C, R>(context, rules);
    }

}
