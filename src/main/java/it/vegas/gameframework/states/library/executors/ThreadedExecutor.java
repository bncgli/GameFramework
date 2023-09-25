package it.vegas.gameframework.states.library.executors;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.statemachines.StateMachine;
import it.vegas.gameframework.states.GameState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Getter
public class ThreadedExecutor<C extends GameContext> extends GameExecutor<C> implements Runnable{

    Executor executor;

    public ThreadedExecutor(GameState<C> startingState) {
        super(startingState);
        executor = Executors.newSingleThreadExecutor();
    }

    public ThreadedExecutor() {
        super();
        executor = Executors.newSingleThreadExecutor();
    }

    public ThreadedExecutor(StateMachine<C> stateMachine) {
        this(stateMachine.getStateTree());
    }

    @Override
    public void execute() {
        executor.execute(this);
    }


    @Override
    public void run() {
        super.execute();
    }


    public static <C extends GameContext> ThreadedExecutor<C> execute(GameState<C> startingState){
        ThreadedExecutor<C> executor = new ThreadedExecutor<>(startingState);
        executor.execute();
        return executor;
    }
}
