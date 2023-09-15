package it.vegas.gameframework.navigators;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import it.vegas.gameframework.contexts.GameContext;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.states.library.structures.GameStateCondition;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * The navigator class contains static methods for
 * visualization of the state machine for debugging purposes
 */
public class MachineRenderer {

    /**
     * Returns the list of gameStates with the direct connections indented
     * following the hierarchy. This method returns the direct connections
     * for the logic connections use the method "renderGraph".
     * WARNING: This machine is susceptible to loops and can cause stack overflow errors
     * @param start The gameState to start to render the machine
     * @param <C> The context object that has to extend GameContext
     */
    public static <C extends GameContext> void renderMachine(GameState<C> start) {
        printMachine(start, "  ");
    }


    /**
     * Iterate recursively all the statemachine printing all the
     * States iterating it
     * @param start The starting node to render the stateMachine
     * @param indent The indentation of the childs of each state
     * @param <C> The context object that has to extend GameContext
     */
    private static <C extends GameContext> void printMachine(GameState<C> start, String indent) {
        System.out.println(indent + start.toString());
        for (GameStateCondition<C> i : start.getNextGameStates()) {

            MachineRenderer.printMachine(i.getResultState(), " " + indent.replace("└─", "  ") + "└─ ");
        }
    }

    /**
     * Extends the DefaultEdge returning the GameStateCondition's
     * expression description instead of the default
     * visualization of the nodes in the graph
     */
    private static class ConditionEdge extends DefaultEdge {
        protected Object condition = null;

        public ConditionEdge(Object condition) {
            this.condition = condition;
        }

        @Override
        public String toString() {
            if (condition == null) {
                return super.toString();
            } else {
                return condition.toString();
            }
        }
    }

    /**
     * Renders a graph with the logic structure of the machine.
     * WARNING: The program rely on the name of the gameState to create nodes,
     * so nodes with the same name are considered the same one, So a good
     * nomenclature is recommended.
     * @param start The starting node where starts to render the statemachine
     * @param branchSpacing (default and min 100) the horizontal space between branches of the graph
     * @param filename (default "graph") the name of the file, written without extension
     * @param <C> The context object that has to extend GameContext
     */
    public static <C extends GameContext> void renderGraph(GameState<C> start, Integer branchSpacing, String filename) {
        DefaultDirectedGraph<String, ConditionEdge> graph = new DefaultDirectedGraph<>(ConditionEdge.class);
        graph = getMachineGraph(start, graph);

        JGraphXAdapter<String, ConditionEdge> graphAdapter = new JGraphXAdapter<>(graph);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.setIntraCellSpacing((branchSpacing != null && branchSpacing > 100) ? branchSpacing : 100);
        layout.setParallelEdgeSpacing(0.0);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 3, Color.WHITE, true, null);
        File imgFile = new File((!Objects.equals(filename, "")) ? filename + ".png" : "graph.png");
        try {
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Iterates the machine to populate the graph
     * with gameStates and GameStateConditions
     * @param start The beginning node where start to navigate the machine
     * @param graph the container of nodes and connection required for rendering of the machine
     * @param <C> The context object that has to extend GameContext
     * @return The container of nodes and connections necessary for the statemachine rendering
     */
    private static <C extends GameContext> DefaultDirectedGraph<String, ConditionEdge> getMachineGraph(GameState<C> start, DefaultDirectedGraph<String, ConditionEdge> graph) {
        if (!graph.containsVertex(start.getName())) {
            graph.addVertex(start.getName());
        }else{
            return graph;
        }
        for (GameStateCondition<C> c : start.getNextGameStates()) {
            graph = getMachineGraph(c.getResultState(), graph);

            if (!graph.containsEdge(start.getName(), c.getResultState().getName())) {
                graph.addEdge(start.getName(), c.getResultState().getName(), new ConditionEdge(c.getExpressionDescription()));
            }
        }
        return graph;
    }

}
