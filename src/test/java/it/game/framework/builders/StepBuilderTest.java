package it.game.framework.builders;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.stateconnections.DirectStateConnection;
import it.game.framework.stateconnections.ExceptionStateConnection;
import it.game.framework.stateconnections.GameStateConnection;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
class StepBuilderTest {

    StateMachine machine;
    TestState a = new TestState();
    TestState b = new TestState();
    TestState c = new TestState();


    static class TestState extends GameState {

        @Override
        public void execute(GameContext c) throws GameException {
            System.out.println("Executing TestState");
        }
    }

    @BeforeEach
    void before() {
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
                .addStartingState(a)
                .addGameState(b)
                .build();

        assertArrayEquals(machine.getStates().toArray(), List.of(a, b).toArray());
    }

    @Test
    void testAddConnection() {
        GameStateConnection conn = new GameStateConnection(
                "a2b",
                a,
                (c) -> true,
                b
        );
        StepBuilder.builder(machine)
                .addStartingState(a)
                .addGameState(b)
                .addConnection(conn)
                .addConnection("b2a", b, (c) -> true, a)
                .build();

        assertEquals(machine.getConnections().get(0), conn);
        assertEquals(machine.getConnections().size(), 2);
    }

    @Test
    void testAddDirectConnection() {
        StepBuilder.builder(machine)
                .addStartingState(a)
                .addGameState(b)
                .addDirectConnection(a, b)
                .build();

        assert (machine.getConnections().get(0) instanceof DirectStateConnection);
    }

    @Test
    void testAddExceptionConnection() {
        StepBuilder.builder(machine)
                .addStartingState(a)
                .addGameState(b)
                .addExceptionConnection("CATCH:9999", new GameException(9999), a, b)
                .build();

        assert (machine.getConnections().get(0) instanceof ExceptionStateConnection);
    }

    @Test
    void testAddGlobalConnection() {
        StepBuilder.builder(machine)
                .addStartingState(a)
                .addGameState(b)
                .addGlobalConnection("global2b", (c) -> true, b)
                .build();

        assertEquals(machine.getGlobalConnections().size(), 1);
    }
}