package it.vegas.gameframework.states.executors;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;
import lombok.Getter;

@Getter
public class ThreadedExecutor<C extends GameContext> extends Executor<C> implements Runnable{

    private Thread thread;


    public ThreadedExecutor(GameState<C> startingState) {
        super(startingState);
    }

    @Override
    public void execute() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        super.execute();
    }
}
