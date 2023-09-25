package it.vegas.gameframework.testgame;

import it.vegas.gameframework.builder.StateTreeBuilder;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.library.executors.GameExecutor;
import it.vegas.gameframework.states.library.structures.GameStateCondition;
import it.vegas.gameframework.testgame.states.CheckWinning;
import it.vegas.gameframework.testgame.states.Spin;
import org.springframework.stereotype.Service;

@Service
public class TestSlotService {

    public TestGameContext context;
    public GameState<TestGameContext> stateMachine;

    public TestSlotService() {
        context = new TestGameContext();

        GameState<TestGameContext> clearContext = new GameState<>("Clear Context");
        clearContext.setAction((s, c) -> c = new TestGameContext());

        GameState<TestGameContext> loadData = new GameState<>("Load user data");
        loadData.setAction((s, c) -> System.out.println("Calling server and storing data"));

        GameState<TestGameContext> spin = new Spin<>();
        GameState<TestGameContext> checkWinningNormal = new CheckWinning<>();
        checkWinningNormal.setName("Check winnings - normal");
        GameState<TestGameContext> checkWinningFreeSpin = new CheckWinning<>();
        checkWinningFreeSpin.setName("Check winnings - freespin");

        GameState<TestGameContext> updateData = new GameState<>("Update data");
        updateData.setAction((s, c) -> System.out.println("Updating data"));


        stateMachine = StateTreeBuilder.builder(context)
                .newGameState(clearContext)
                .setNextGameState(loadData)
                .setNextGameState(spin)
                .setBranchingStates(
                        new GameStateCondition<>(
                                "fs = t",
                                (s, c) -> !context.isFreeSpin,
                                StateTreeBuilder.builder(context)
                                        .newGameState(checkWinningNormal)

                                        .setBranchingStates(
                                                GameStateCondition.create(
                                                        "fs > 0",
                                                        (s, c) -> c.freeSpins > 0,
                                                        spin
                                                ),
                                                GameStateCondition.create(
                                                        "fs = 0",
                                                        (s, c) -> c.freeSpins == 0,
                                                        updateData
                                                )
                                        )
                        ),
                        new GameStateCondition<>(
                                "fs = f",
                                (s, c) -> context.isFreeSpin,
                                StateTreeBuilder.builder(context)
                                        .newGameState(checkWinningFreeSpin)


                                        .setBranchingStates(
                                                GameStateCondition.create(
                                                        "fs > 0",
                                                        (s, c) -> c.freeSpins > 0,
                                                        spin
                                                ),
                                                GameStateCondition.create(
                                                        "fs = 0",
                                                        (s, c) -> c.freeSpins == 0,
                                                        updateData
                                                )
                                        )
                        )
                );
    }

    public String spin() {
        GameExecutor<TestGameContext> executor = new GameExecutor<>(stateMachine);
        executor.execute();
        return context.toString();
    }


}
