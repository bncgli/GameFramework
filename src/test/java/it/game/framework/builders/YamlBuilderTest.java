package it.game.framework.builders;

import it.game.framework.renderers.Renderer;
import it.game.framework.stateconnections.DirectStateConnection;
import it.game.framework.stateconnections.ExceptionStateConnection;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.testclasses.GameStateA;
import it.game.framework.testclasses.GameStateB;
import it.game.framework.testclasses.GameStateC;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@RunWith(SpringRunner.class)
class YamlBuilderTest {

    @Autowired
    Renderer renderer;

    String yamlpath = "poc.yaml";

    @Test
    void build() throws Exception {
        StateMachine machine = new StateMachine();
        YamlBuilder.builder(machine, yamlpath).build();

        renderer.renderTree(machine);
        renderer.renderGraph(machine, "yamlGraph");

        assertEquals(machine.getStates().size(), 3);
        assert(machine.getStates().get(0) instanceof GameStateA);
        assert(machine.getStates().get(1) instanceof GameStateB);
        assert(machine.getStates().get(2) instanceof GameStateC);

        assertEquals(machine.getConnections().size(), 3);
        assert(machine.getConnections().get(0).getStartingState() instanceof GameStateA && machine.getConnections().get(0).getResultState() instanceof GameStateB);
        assert(machine.getConnections().get(1).getStartingState() instanceof GameStateA && machine.getConnections().get(1).getResultState() instanceof GameStateC);
        assert(machine.getConnections().get(1) instanceof DirectStateConnection);
        assert(machine.getConnections().get(1).checkExpression(null));
        assert(machine.getConnections().get(2).getStartingState() instanceof GameStateB && machine.getConnections().get(2).getResultState() instanceof GameStateC);

        assertEquals(machine.getGlobalConnections().size(), 2);
        assert(machine.getGlobalConnections().get(0).getStartingState() == null && machine.getGlobalConnections().get(0).getResultState() instanceof GameStateB);
        assert(machine.getGlobalConnections().get(1).getStartingState() == null && machine.getGlobalConnections().get(1).getResultState() == null);
        assert(machine.getGlobalConnections().get(1) instanceof ExceptionStateConnection);


    }
}