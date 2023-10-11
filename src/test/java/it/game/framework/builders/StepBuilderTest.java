package it.game.framework.builders;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@RunWith(SpringRunner.class)
class StepBuilderTest {

    StateMachine machine;
    TestState a = new TestState();
    TestState b = new TestState();
    TestState c = new TestState();


    static class TestState extends GameState{

        @Override
        public void execute(GameContext c) throws GameException {
            System.out.println("Executing TestState");
        }
    }

    @Before
    void before(){
        machine = new StateMachine();
    }

    @Test
    void addStartingState() {
        StepBuilder.builder(machine)
                .addStartingState(a)
                .build();

        assertEquals(a, machine.getStartState());
    }

    @Test
    void addGameState() {
        StepBuilder.builder(machine)
                .addGameState(a)
                .addGameState(b)
                .build();

        assertArrayEquals(machine.getStates().toArray(), List.of(a, b).toArray());
    }

    @Test
    void testAddConnection() {
    }

    @Test
    void testAddDirectConnection() {
    }

    @Test
    void testAddConnectionFromLastState() {
    }

    @Test
    void build() {
    }
}