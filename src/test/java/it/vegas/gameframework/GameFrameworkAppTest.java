package it.vegas.gameframework;


import it.vegas.gameframework.builder.StateMachineBuilder;
import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.navigators.MachineRenderer;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.interfaces.actions.GameStateAction;
import it.vegas.gameframework.states.library.executors.Executor;
import it.vegas.gameframework.states.library.structures.GameStateCondition;
import it.vegas.gameframework.testgame.TestSlotService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
class GameFrameworkAppTest {

    private static class TestContext extends GameContext {
        public int branchingTest;
    }

    @Test
    void buildTest() {
        TestContext c = new TestContext();
        GameStateAction<TestContext> action = (s, x) -> System.out.println("Executing " + s.getName() + " current context status: " + s.getContext().branchingTest);

        GameState<TestContext> commonEnd = StateMachineBuilder.builder(c)
                .newGameState()
                .setName("Common End")
                .setAction(action)
                .build();

        GameState<TestContext> testMachine = StateMachineBuilder.builder(c)
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
                                StateMachineBuilder.builder(c)
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
                                StateMachineBuilder.builder(c)
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
                                                        StateMachineBuilder.builder(c)
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
                                                        StateMachineBuilder.builder(c)
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
                                StateMachineBuilder.builder(c)
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


        MachineRenderer.renderMachine(testMachine);
        //       MachineRenderer.renderGraph(testMachine, 100, "graph3");

        Executor<TestContext> exec = new Executor<>(testMachine);
        exec.execute();
    }

    @Test
    void gameTest(){
        TestSlotService service = new TestSlotService();

        MachineRenderer.renderMachine(service.stateMachine);
        //MachineRenderer.renderGraph(service.stateMachine,100, "looping");

        for (int i = 0; i < 10; i++) {
            System.out.println(service.spin());
            System.out.println("\n\n\n\n");
        }
    }
}