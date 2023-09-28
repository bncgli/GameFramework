package it.game.framework;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.executors.MonitoredExecutor;
import it.game.framework.executors.SteppedExecutor;
import it.game.framework.executors.library.TimingMonitor;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
public class gameFrameworkAppTest {

    public static class TestContext extends GameContext {
        public int fs = 0;
        public int spinResult = 0;
    }

    public static class LoadPlayerData extends GameState<TestContext> {

        @Override
        public void execute(TestContext context) throws GameException {
            System.out.println("Loading player data into context from db");
        }
    }

    public static class Spin extends GameState<TestContext> {

        @Override
        public void execute(TestContext context) throws GameException {
            if (context.fs > 0) {
                System.out.println("Executing freeSpin");
                return;
            }
            System.out.println("Executed Spin with result: " + context.spinResult);
        }
    }

    public static class CalculateFreeSpin extends GameState<TestContext> {

        @Override
        public void execute(TestContext context) throws GameException {
            context.fs--;
            System.out.println("Executed CalculateFreeSpin, remaining freespin: " + context.fs);
        }
    }

    public static class CalculateNormalSpin extends GameState<TestContext> {

        @Override
        public void execute(TestContext context) throws GameException {
            System.out.println("Executed CalculateNormalSpin");
            if (context.spinResult > 5) {
                context.fs = 3;
                System.out.println("Won 3 freeSpin");
            }
        }
    }

    public static class UpdatePlayerData extends GameState<TestContext> {

        @Override
        public void execute(TestContext context) throws GameException {
            System.out.println("Updating data on server");
        }
    }

    public StateMachine<TestContext> machine;
    public TestContext testContext;


    @Before
    public void test() {
        testContext = new TestContext();
        machine = new StateMachine<>(testContext);

        LoadPlayerData load = new LoadPlayerData();
        Spin spin = new Spin();
        CalculateFreeSpin calcFree = new CalculateFreeSpin();
        CalculateNormalSpin calcNorm = new CalculateNormalSpin();
        UpdatePlayerData update = new UpdatePlayerData();

        machine.builder()
                .addStartingState(load)
                .addConnectionFromLastState(spin)
                .addGameState(spin)
                .addConnectionFromLastState("fs > 0", s -> s.fs > 0, calcFree)
                .addConnectionFromLastState("fs = 0", s -> s.fs == 0, calcNorm)
                .addGameState(calcNorm)
                .addConnectionFromLastState("fs > 0", s -> s.fs > 0, spin)
                .addConnectionFromLastState(update)
                .addGameState(calcFree)
                .addConnectionFromLastState("fs > 0", s -> s.fs > 0, spin)
                .addConnectionFromLastState(update)
                .addGameState(update)
                .build();

        System.out.println(machine.renderer().tree());
        machine.renderer().graph("graph");


    }

    @Test
    public void testSteppedExecutor() {
        testContext.spinResult = 6;

        machine.setExecutor(new SteppedExecutor<>());
        SteppedExecutor<TestContext> steppedExecutor = (SteppedExecutor<TestContext>) machine.executor();

        for (Optional<GameState<TestContext>> c : steppedExecutor) {
            System.out.println("next step: " + c.map(GameState::ID).orElse("null"));
            System.out.println("\n\n\n");
        }
    }

    @Test
    public void testMonitoredExecutor() {
        testContext.spinResult = 6;

        TimingMonitor<TestContext> timingMonitor = new TimingMonitor<>();
        machine.setExecutor(new MonitoredExecutor<>(timingMonitor));

        machine.executor().execute();

        System.out.println("Total execution: " + timingMonitor.getTotalExecution());
        System.out.println("Execution of stages:\n" + timingMonitor.getStateExecution().stream().map(v -> v.getClass() + "\t" + v.getKey()).collect(Collectors.joining("\n")));
    }

}