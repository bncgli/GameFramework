package it.game.framework.executors;

import it.game.framework.contexts.GameContext;
import it.game.framework.statemachines.StateMachine;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Getter
public class ThreadedExecutor<C extends GameContext<C>> extends GameExecutor<C> implements Runnable{

    Executor executor;

    public ThreadedExecutor() {
        this(null);
    }

    public ThreadedExecutor(StateMachine<C> stateMachine) {
        super(stateMachine);
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute() {
        executor.execute(this);
    }

    @Override
    public void run() {
        super.execute();
    }

}
