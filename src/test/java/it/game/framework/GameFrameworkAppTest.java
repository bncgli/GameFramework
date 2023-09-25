package it.game.framework;


import it.game.framework.serializations.Serializer;
import it.game.framework.testgame.TestGameContext;
import it.game.framework.builder.StateTreeBuilder;
import it.game.framework.contexts.GameContext;
import it.game.framework.serializations.Deserializer;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import it.game.framework.states.interfaces.actions.GameStateAction;
import it.game.framework.states.library.executors.GameExecutor;
import it.game.framework.states.library.structures.GameStateCondition;
import it.game.framework.testgame.TestSlotService;
import it.game.framework.testgame.states.Spin;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import java.util.Random;

@SpringBootTest
@PropertySource("classpath:application.properties")
class GameFrameworkAppTest {

    private static class TestContext extends GameContext {
        public int branchingTest;
    }

    @Test
    void buildTest() {
        TestContext c = new TestContext();
        GameStateAction<TestContext> action = (s, x) -> System.out.println("Executing " + s.getName() + " current context status: " + s.getContext().branchingTest);

        GameState<TestContext> commonEnd = StateTreeBuilder.builder(c)
                .newGameState()
                .setName("Common End")
                .setAction(action)

                .setNextGameState()
                .setName("Common End Second Phase")
                .setAction(action)

                .build();

        GameState<TestContext> testMachine =
                StateTreeBuilder.builder(c)
                .newGameState()
                .setName("Fase 1")
                .setAction(action)

                .setNextGameState()
                .setName("Fase 2")
                .setAction((s, x) -> {
                    s.getContext().branchingTest = new Random().nextInt(1, 4);
                    System.out.println("Branching value: " + s.getContext().branchingTest);
                })
                .setBranchingStates(
                        new GameStateCondition<>(
                                "state = 1",
                                (s, context) -> s.getContext().branchingTest == 1,
                                StateTreeBuilder.builder(c)
                                        .newGameState()
                                        .setName("Branch 1.1")
                                        .setAction(action)

                                        .setNextGameState()
                                        .setName("Branch 1.2")
                                        .setAction(action)

                                        .setNextGameState(commonEnd)
                                        .build()
                        ),
                        new GameStateCondition<>(
                                "state = 2",
                                (s, context) -> s.getContext().branchingTest == 2,
                                StateTreeBuilder.builder(c)
                                        .newGameState()
                                        .setName("Branch 2.1")
                                        .setAction(action)

                                        .setNextGameState()
                                        .setName("Branch 2.2")
                                        .setAction(action)
                                        .setBranchingStates(
                                                new GameStateCondition<>(
                                                        "state = 1",
                                                        (s, context) -> s.getContext().branchingTest == 1,
                                                        StateTreeBuilder.builder(c)
                                                                .newGameState()
                                                                .setName("Branch 2.2.1.1")
                                                                .setAction(action)

                                                                .setNextGameState()
                                                                .setName("Branch 2.2.1.2")
                                                                .setAction(action)

                                                                .setNextGameState(commonEnd)
                                                                .build()
                                                ),
                                                new GameStateCondition<>(
                                                        "state = 2",
                                                        (s, context) -> s.getContext().branchingTest == 2,
                                                        StateTreeBuilder.builder(c)
                                                                .newGameState()
                                                                .setName("Branch 2.2.2.1")
                                                                .setAction(action)

                                                                .setNextGameState()
                                                                .setName("Branch 2.2.2.2")
                                                                .setAction(action)

                                                                .setNextGameState(commonEnd)
                                                                .build()
                                                )
                                        )
                        ),
                        new GameStateCondition<>(
                                "state = 3",
                                (s, context) -> s.getContext().branchingTest == 3,
                                StateTreeBuilder.builder(c)
                                        .newGameState()
                                        .setName("Branch 3.1")
                                        .setAction(action)

                                        .setNextGameState()
                                        .setName("Branch 3.2")
                                        .setAction(action)

                                        .setNextGameState(commonEnd)
                                        .build()
                        )
                );

        StateMachine<TestContext> stateMachine = StateMachine.create(testMachine);
//        System.out.println(stateMachine.getGameStateSet().stream().map(GameState::getName).collect(Collectors.joining("\n")));

        stateMachine.renderGraph("graph");
//
//
//        Serializer.save(testMachine, "testSerialization");
//        GameState<TestContext> loadedmachine = Deserializer.load("testSerialization", GameState.class);
//
//        MachineRenderer.renderGraph(loadedmachine, "LoadedMachine");
//
//        GameExecutor<TestContext> exec = new GameExecutor<>(loadedmachine);
//        exec.execute();
    }

    @Test
    void gameTest() {
        TestSlotService service = new TestSlotService();

        //MachineRenderer.renderGraph(service.stateMachine,100, "looping");

        for (int i = 0; i < 10; i++) {
            System.out.println(service.spin());
            System.out.println("\n\n\n\n");
        }
    }

    @Test
    void serializationTest() {
        TestGameContext context = new TestGameContext();

        GameState<TestGameContext> storedMachine = StateTreeBuilder.builder(context)
                .newGameState()
                .setName("Begin")
                .setAction((s, c) -> System.out.println("Executing begin"))

                .setNextGameState(new Spin<>())

                .setNextGameState()
                .setName("End")
                .setAction((s, c) -> System.out.println("Executing ending"))
                .build();


        Serializer.save(storedMachine, "storedMachine");
        GameState<TestContext> loadedMachine = Deserializer.load("storedMachine", GameState.class);

        GameExecutor<TestGameContext> execute = GameExecutor.execute(storedMachine);
        System.out.println("\n\n\n\n");
        GameExecutor<TestContext> execute1 = GameExecutor.execute(loadedMachine);

        System.out.println(execute.getContext());
        System.out.println(execute1.getContext());


    }
}