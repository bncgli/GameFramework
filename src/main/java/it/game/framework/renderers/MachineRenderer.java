package it.game.framework.renderers;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import it.game.framework.contexts.GameContext;
import it.game.framework.states.GameState;
import it.game.framework.states.library.structures.GameStateCondition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
public final class MachineRenderer {

    /**
     * Extends the DefaultEdge returning the GameStateCondition's
     * expression description instead of the default
     * visualization of the nodes in the graph
     */
    private static class ConditionEdge extends DefaultEdge {
        protected Object condition;

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

    @Builder
    @Getter
    @Setter
    public static class GraphSpecifics {
        @Builder.Default
        private int orientation = 1;
        @Builder.Default
        private double horizontalSpacing = 100;
        @Builder.Default
        private double verticalSpacing = 50;
        @Builder.Default
        private Color backgroundColor = Color.WHITE;
        @Builder.Default
        private int graphScale = 3;
    }

    /**
     * Renders a graph with the logic structure of the machine.
     * WARNING: The program rely on the name of the gameState to create nodes,
     * so nodes with the same name are considered the same one, So a good
     * nomenclature is recommended.
     *
     * @param start    The starting node where starts to render the statemachine
     * @param filename (default "graph") the name of the file, written without extension
     * @param specs    The object GraphSpecifics that contains all the default data
     * @param <C>      The context object that has to extend GameContext
     */
    public static <C extends GameContext> void renderGraph(GameState<C> start, String filename, GraphSpecifics specs) {
        GraphSpecifics graphSpecifics = (specs != null) ? specs : GraphSpecifics.builder().build();

        DefaultDirectedGraph<String, ConditionEdge> graph = new DefaultDirectedGraph<>(ConditionEdge.class);
        graph = getMachineGraph(start, graph);

        JGraphXAdapter<String, ConditionEdge> graphAdapter = new JGraphXAdapter<>(graph);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.setOrientation(graphSpecifics.getOrientation());
        layout.setIntraCellSpacing(graphSpecifics.getHorizontalSpacing());
        layout.setInterRankCellSpacing(graphSpecifics.getVerticalSpacing());
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(
                graphAdapter,
                null,
                graphSpecifics.getGraphScale(),
                graphSpecifics.getBackgroundColor(),
                true,
                null
        );
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
     *
     * @param start The beginning node where start to navigate the machine
     * @param graph the container of nodes and connection required for rendering of the machine
     * @param <C>   The context object that has to extend GameContext
     * @return The container of nodes and connections necessary for the statemachine rendering
     */
    private static <C extends GameContext> DefaultDirectedGraph<String, ConditionEdge> getMachineGraph(GameState<C> start, DefaultDirectedGraph<String, ConditionEdge> graph) {
        if (!graph.containsVertex(start.getName())) {
            graph.addVertex(start.getName());
        } else {
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
