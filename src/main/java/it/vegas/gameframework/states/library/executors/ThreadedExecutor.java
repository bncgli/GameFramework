package it.vegas.gameframework.states.library.executors;

import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ThreadedExecutor<C extends GameContext> extends Executor<C> implements Runnable{

    private Thread thread;
    private boolean stop;
    private boolean pause;

    public ThreadedExecutor(GameState<C> startingState) {
        super(startingState);
        stop = false;
        pause = false;
    }

    @Override
    public void execute() {
        thread = new Thread(this);
        thread.start();
    }


    @Override
    public void run() {
        currentState = startingState;
        try {
            while (currentState != null) {
                if(stop) break;
                if(pause){
                    Thread.sleep(250);
                    continue;
                }
                log.info("Entering gameState: {}", currentState.getName());
                currentState.execute();
                log.info("Exiting gameState: {}", currentState.getName());
                currentState = currentState.getNextGameState();
            }
        } catch (Exception e) {
            log.error(
                    "Error occurred executing GameState {} with errorID {} and message:\n{}",
                    currentState,
                    e.hashCode(),
                    e.getMessage()
            );
        }
    }

    public void stop(){
        stop = true;
    }
}
