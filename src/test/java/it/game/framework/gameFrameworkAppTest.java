package it.game.framework;

import it.game.framework.builders.YamlBuilder;
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

    public static class LoadData extends GameState<TestContext> {

        @Override
        public void execute(TestContext context) throws GameException {
            System.out.println("Loading player data into context from db");
        }
    }

    public static class Elaboration extends GameState<TestContext> {

        @Override
        public void execute(TestContext context) throws GameException {
            if (context.fs > 0) {
                System.out.println("Executing freeSpin");
                return;
            }
            System.out.println("Executed Elaboration with result: " + context.spinResult);
        }
    }

    public static class StoreData extends GameState<TestContext> {

        @Override
        public void execute(TestContext context) throws GameException {
            context.fs--;
            System.out.println("Executed StoreData, remaining freespin: " + context.fs);
        }
    }

    public static class SendData extends GameState<TestContext> {

        @Override
        public void execute(TestContext context) throws GameException {
            System.out.println("Executed SendData");
            if (context.spinResult > 5) {
                context.fs = 3;
            }
        }
    }

    public static class UpdateData extends GameState<TestContext> {

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

        LoadData load = new LoadData();
        Elaboration spin = new Elaboration();
        StoreData calcFree = new StoreData();
        SendData calcNorm = new SendData();
        UpdateData update = new UpdateData();

        machine.builder()
                .addStartingState(load)
                .addConnectionFromLastState(spin)
                .addGameState(spin)
                .addConnectionFromLastState("fs>0", s -> s.fs > 0, calcFree)
                .addConnectionFromLastState("fs=0", s -> s.fs == 0, calcNorm)
                .addGameState(calcNorm)
                .addConnectionFromLastState("fs>0", s -> s.fs > 0, spin)
                .addConnectionFromLastState(update)
                .addGameState(calcFree)
                .addConnectionFromLastState("fs>0", s -> s.fs > 0, spin)
                .addConnectionFromLastState(update)
                .addGameState(update)
                .build();




    }

    @Test
    public void testRender(){
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

    @Test
    public void testYamlBuilder(){
        StateMachine<TestContext> m = new StateMachine<>();
        YamlBuilder<TestContext> builder = new YamlBuilder<>(m,"poc.yaml");
        builder.build();
    }

}