package it.game.framework.executors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This executor creates and executes the StateMachine
 * in a separate Thread.
 */
@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ThreadedExecutor extends GameExecutor implements Runnable{

    Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void execute() {
        executor.execute(this);
    }

    @Override
    public void run() {
        super.execute();
    }

}
