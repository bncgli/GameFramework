package it.game.framework.executors.library;

import it.game.framework.contexts.GameContext;
import it.game.framework.executors.interfaces.ExecutorCallback;
import it.game.framework.states.GameState;
import lombok.Getter;

import java.util.*;

public class TimingCallback extends ExecutorCallback {

    @Getter
    private long totalExecution = 0L;
    @Getter
    private List<AbstractMap.SimpleEntry<String, Long>> stateExecution = new LinkedList<>();
    private final Date date = new Date();

    @Override
    public void beforeLoop(GameContext context) {
        totalExecution = date.getTime();
    }

    @Override
    public void beforeExecution(GameState currentState, GameContext context) {
        stateExecution.add(0, new AbstractMap.SimpleEntry<>(currentState.ID(), date.getTime()));
    }

    @Override
    public void afterExecution(GameState currentState, GameContext context) {
        AbstractMap.SimpleEntry<String, Long> last = stateExecution.get(0);
        last.setValue(date.getTime() - last.getValue());
    }

    @Override
    public void afterLoop(GameContext context) {
        totalExecution = date.getTime() - totalExecution;
        Collections.reverse(stateExecution);
    }

}
