package it.game.framework.executors;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ThreadedExecutor extends GameExecutor implements Runnable{

    Executor executor = Executors.newSingleThreadExecutor();
    GameExecutor gameExecutor = new GameExecutor();

    @Override
    public void execute() {
        executor.execute(this);
    }

    @Override
    public void run() {
        try {
            if (gameExecutor == null) throw new GameException(GameExceptionsLibrary.GAMEEXECUTOR_IS_NULL);
            gameExecutor.execute();
        }catch (Exception e){
            log.error(GameException.format(e));
        }
    }

    @Override
    public GameContext getContext() {
        return gameExecutor.getContext();
    }

    @Override
    public GameState getCurrentGameState() {
        return gameExecutor.getCurrentGameState();
    }

    @Override
    public StateMachine getStateMachine() {
        return gameExecutor.getStateMachine();
    }

    @Override
    public void setContext(GameContext context) {
        gameExecutor.setContext(context);
    }

    @Override
    public void setCurrentGameState(GameState currentGameState) {
        gameExecutor.setCurrentGameState(currentGameState);
    }

    @Override
    public void setStateMachine(StateMachine stateMachine) {
        gameExecutor.setStateMachine(stateMachine);
    }
}
