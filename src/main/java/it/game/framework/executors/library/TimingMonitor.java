package it.game.framework.executors.library;

import it.game.framework.contexts.GameContext;
import it.game.framework.executors.interfaces.ExecutorMonitor;
import it.game.framework.states.GameState;
import lombok.Getter;

import java.util.*;

public class TimingMonitor<C extends GameContext> implements ExecutorMonitor<C> {

    @Getter
    private long totalExecution = 0L;
    @Getter
    private List<AbstractMap.SimpleEntry<String, Long>> stateExecution = new LinkedList<>();
    private final Date date = new Date();

    @Override
    public void beforeLoop() {
        totalExecution = date.getTime();
    }

    @Override
    public void beforeExecution(GameState<C> currentState) {
        stateExecution.add(0, new AbstractMap.SimpleEntry<>(currentState.ID(), date.getTime()));
    }

    @Override
    public void afterExecution(GameState<C> currentState) {
        AbstractMap.SimpleEntry<String, Long> last = stateExecution.get(0);
        last.setValue(date.getTime() - last.getValue());
    }

    @Override
    public void afterLoop() {
        totalExecution = date.getTime() - totalExecution;
        Collections.reverse(stateExecution);
    }

    @Override
    public void nextSelectedGameState(GameState<C> currentState, GameState<C> nextGameState) {
    }

    @Override
    public void caughtException(GameState<C> currentState, Exception e) {

    }
}
