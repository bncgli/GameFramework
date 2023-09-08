package it.vegas.gameframework;


import it.vegas.gameframework.builder.StateMachineBuilder;
import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.executors.Executor;
import it.vegas.gameframework.navigators.Navigator;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.interfaces.actions.GameStateAction;
import it.vegas.gameframework.states.library.structures.GameStateCondition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestContext;

import java.util.Random;

@SpringBootTest
class gameframeworkapptest {

    private static class TestContext extends GameContext{
        public boolean branchingTest;
    }

    @Test
    void buildTest() {
        TestContext c = new TestContext();
        GameStateAction<TestContext> action = (s) -> System.out.println("Executing " + s.getName()+ " current context status: "+s.getContext().branchingTest);
        GameState<TestContext> testMachine = StateMachineBuilder.builder(c)
                .newGameState()
                .setName("Fase 1")
                .setAction(action)

                .setNextGameState()
                .setName("Fase 2")
                .setAction((s)->{
                    s.getContext().branchingTest = new Random().nextBoolean();
                    System.out.println("Branching value: "+s.getContext().branchingTest);
                })
                .setBranchingStates(
                        new GameStateCondition<>(
                                (s) -> s.getContext().branchingTest,
                                StateMachineBuilder.builder(c)
                                        .newGameState()
                                        .setName("Branch 1 - Fase 1")
                                        .setAction(action)

                                        .setNextGameState()
                                        .setName("Branch 1 - Fase 2")
                                        .setAction(action)
                                        .build()
                        ),
                        new GameStateCondition<>(
                                (s) -> !s.getContext().branchingTest,
                                StateMachineBuilder.builder(c)
                                        .newGameState()
                                        .setName("Branch 2 - Fase 1")
                                        .setAction(action)

                                        .setNextGameState()
                                        .setName("Branch 2 - Fase 2")
                                        .setAction(action)
                                        .build()
                        )
                );



        Navigator.printMachine(testMachine);

        Executor<TestContext> exec = new Executor<>(testMachine);
        exec.execute();
    }
}