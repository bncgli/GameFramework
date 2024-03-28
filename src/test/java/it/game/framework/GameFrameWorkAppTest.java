package it.game.framework;

import it.game.framework.builders.StepBuilder;
import it.game.framework.builders.YamlBuilder;
import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.ExceptionLibrary;
import it.game.framework.executors.GameExecutor;
import it.game.framework.executors.SteppedExecutor;
import it.game.framework.executors.library.TimingCallback;
import it.game.framework.renderers.Renderer;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class GameFrameWorkAppTest {

    public static class LoadData extends GameState {

        @Override
        public void execute(GameContext c) {
            System.out.println("Loading player data into context from db");
        }
    }

    public static class Spin extends GameState {

        @Override
        public void execute(GameContext c) {
            System.out.println("Executing the spin");
        }
    }

    public static class CheckNormalSpin extends GameState {

        @Override
        public void execute(GameContext c) {
            System.out.println("Checking the normal spin results");
        }
    }

    public static class CheckFreeSpin extends GameState {

        @Override
        public void execute(GameContext c) {
            System.out.println("Checking the free spin results");
        }
    }

    public static class UpdatePlayerData extends GameState {

        @Override
        public void execute(GameContext c) {
            System.out.println("Update player data");
        }
    }


    public StateMachine machine;

    @Before
    public void test() {
        machine = new StateMachine();

        LoadData load = new LoadData();
        Spin spin = new Spin();
        CheckNormalSpin calcFree = new CheckNormalSpin();
        CheckFreeSpin calcNorm = new CheckFreeSpin();
        UpdatePlayerData update = new UpdatePlayerData();

        StepBuilder.builder(machine)
                .addStartingState(load)
                .addDirectConnectionFromLastState(spin)
                .addGameState(spin)
                .addConnectionFromLastState("fs>0", (c) -> c.<Integer>get("fs") > 0, calcFree)
                .addConnectionFromLastState("fs=0", (c) -> c.<Integer>get("fs") == 0, calcNorm)
                .addGameState(calcNorm)
                .addConnectionFromLastState("fs>0", (c) -> c.<Integer>get("fs") > 0, spin)
                .addDirectConnectionFromLastState(update)
                .addGameState(calcFree)
                .addConnectionFromLastState("fs>0", (c) -> c.<Integer>get("fs") > 0, spin)
                .addDirectConnectionFromLastState(update)
                .addGameState(update)
                .addGlobalConnection("fs<0", (c) -> c.<Integer>get("fs") < 0, update)
                .build();
    }

    @Autowired
    Renderer renderer;

    @Test
    public void testRender() {

        System.out.println(renderer.renderTree(machine));
        renderer.renderGraph(machine, "graph");
    }

    @Autowired
    SteppedExecutor stepExecutor;

    @Test
    public void testSteppedExecutor() {
        stepExecutor.setStateMachine(machine);
        stepExecutor.setContext(null);

        for (var c : stepExecutor) {
            System.out.println("next step: " + c.map(GameState::ID).orElse("null"));
            System.out.println("\n\n\n");
        }
    }


    @Autowired
    GameExecutor gameExecutor;

    @Test
    public void testMonitoredExecutor() {

        TimingCallback timingMonitor = new TimingCallback();

        gameExecutor.setStateMachine(machine);
        gameExecutor.setContext(null);
        gameExecutor.setCallbacks(timingMonitor);

        System.out.println("Total execution: " + timingMonitor.getTotalExecution());
        System.out.println("Execution of stages:\n" + timingMonitor.getStateExecution().stream().map(v -> v.getClass() + "\t" + v.getKey()).collect(Collectors.joining("\n")));
    }

    @Test
    public void testYamlBuilder() {
        StateMachine m = new StateMachine();
        YamlBuilder.builder(m, "poc.yaml").build();

        System.out.println(renderer.renderTree(m));
        renderer.renderGraph(m, "yamlBuilderTest");
    }

    @Test
    public void TestRenderExceptions(){
        System.out.println(ExceptionLibrary.printExceptions());
    }

}