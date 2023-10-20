package it.game.framework.renderers;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * This component contain methods for the visualization of the machine
 */
@Component
public class Renderer {
    /**
     * Sets the orientation of the graph.
     * the default is 1 (top to bottom),
     * an alternative is 7(left to right)
     */
    private int orientation = 1;
    /**
     * Represents the horizontal spacing between cells.
     * The default value is 100, when condition descriptions
     * overlap is better to increase this value to make space
     * for them.
     */
    private double horizontalSpacing = 100;
    /**
     * Represents the vertical spacing between cells.
     * The default value is 50.
     */
    private double verticalSpacing = 50;
    /**
     * The scale of the rendered image.
     * The default scale is 3.
     */
    private int graphScale = 3;

    public Renderer(
            @Value("${game.framework.renderer.orientation}") String orientation,
            @Value("${game.framework.renderer.horizontal_spacing}") double horizontalSpacing,
            @Value("${game.framework.renderer.vertical_spacing}") double verticalSpacing,
            @Value("${game.framework.renderer.image_scale}") int graphScale
    ) {
        this.orientation = (orientation.compareTo("horizontal") == 0 ? 7 : 1);
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
        this.graphScale = graphScale;
    }

    /**
     * Extends the DefaultEdge returning the GameStateConnection's
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
                return "";
            } else {
                return condition.toString();
            }
        }
    }

    /**
     * Renders the machine as a tree
     * @param machine The state machine we want to render
     * @return A string that can be print representing the state machine as a tree
     */
    public String renderTree(StateMachine machine) {
        StringBuilder res = new StringBuilder();
        for (GameState s : machine.getStates()) {
            res.append(s.ID()).append("\n");
            machine.getConnectionsOf(s).forEach(v -> res.append("\t").append(v).append("\n"));
        }
        return res.toString();
    }

    /**
     * Renders a graph with the logic structure of the machine.
     * WARNING: The program rely on the name of the GameState to create nodes,
     * so nodes with the same name are considered the same one, So a good
     * nomenclature is recommended.
     *
     * @param filename (default "graph") the name of the file, written without extension
     */
    public void renderGraph(StateMachine machine, String filename) {
        DefaultDirectedGraph<String, ConditionEdge> graph = new DefaultDirectedGraph<>(ConditionEdge.class);
        machine.getStates().forEach(v -> graph.addVertex(v.ID()));
        graph.addVertex("GLOBAL");
        machine.getConnections().forEach(v -> {
            graph.addEdge(
                    v.getStartingState() == null ? "GLOBAL" : v.getStartingState().ID(),
                    v.getResultState().ID(),
                    new ConditionEdge(v.getExpressionDescription())
            );
        });

        JGraphXAdapter<String, ConditionEdge> graphAdapter = new JGraphXAdapter<>(graph);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.setOrientation(orientation);
        layout.setIntraCellSpacing(horizontalSpacing);
        layout.setInterRankCellSpacing(verticalSpacing);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(
                graphAdapter,
                null,
                graphScale,
                Color.WHITE,
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

}
