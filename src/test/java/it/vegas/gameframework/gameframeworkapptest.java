package it.vegas.gameframework;

import it.vegas.gameframework.builders.StateMachineBuilder;
import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.enums.ActionReturn;
import it.vegas.gameframework.executors.Executor;
import it.vegas.gameframework.rules.GameRules;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.interfaces.actions.GameStateAction;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class gameframeworkapptest {

    @Test
    void contextLoads() {
        GameContext c = new GameContext();
        GameRules r = new GameRules();
        GameStateAction<GameContext, GameRules> a = (self, context, rules)->{
            System.out.println(self.getName());
            return ActionReturn.OK;
        };
        GameState<GameContext,GameRules> s = StateMachineBuilder.builder(c, r)
                .newState()
                .setName("SottoFase1")
                .setAction(a)

                .goToNewState()
                .setName("SottoFase2")
                .setAction(a)

                .goToNewState()
                .setName("SottoFase3")
                .setAction(a)
                .build();

        System.out.println(s.toString());
        System.out.println(s.getNextGameState().toString());
        System.out.println(s.getNextGameState().getNextGameState().toString());

        Executor<GameContext,GameRules> e = new Executor<>();
        e.setStartingState(s);

        GameState<GameContext,GameRules> s1 = StateMachineBuilder.builder(c, r)
                .newState()
                .setName("Fase1")
                .setAction(new GameRules.testRule<>())

                .goToState(e)
                .setName("Fase2")

                .goToNewState()
                .setName("Fase3")
                .setAction(a)
                .build();

        System.out.println(s1.toString());
        System.out.println(s1.getNextGameState().toString());
        System.out.println(s1.getNextGameState().getNextGameState().toString());

        Executor<GameContext, GameRules> d = new Executor<>();
        d.setStartingState(s1);
        d.execute();
    }

}