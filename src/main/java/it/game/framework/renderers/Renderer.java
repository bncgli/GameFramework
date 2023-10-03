package it.game.framework.renderers;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.imageio.ImageIO;
import javax.swing.plaf.nimbus.State;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * The navigator class contains static methods for
 * visualization of the state machine for debugging purposes
 */
public class Renderer {

    private final StateMachine machine;

    @Getter
    private final GraphSpecifics graphSettings;

    public Renderer(StateMachine machine) {
        this.machine = machine;
        graphSettings = new GraphSpecifics();
    }

    public static Renderer renderer(StateMachine machine){
        return new Renderer(machine);
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
     * GraphSpecifics contains a list of variables that are necessary for the
     * graph method to render the statemachine.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class GraphSpecifics {
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
         * The background color of the image.
         * The default color is WHITE.
         */
        private Color backgroundColor = Color.WHITE;
        /**
         * The scale of the rendered image.
         * The default scale is 3.
         */
        private int graphScale = 3;
    }

    public String tree() {
        StringBuilder res = new StringBuilder();
        for (GameState s : machine.getStates()) {
            res.append(s.ID()).append("\n");
            machine.getConnectionsOf(s).forEach(v->res.append("    ").append(v).append("\n"));
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
    public void graph(String filename) {
        DefaultDirectedGraph<String, ConditionEdge> graph = new DefaultDirectedGraph<>(ConditionEdge.class);
        machine.getStates().forEach(v -> graph.addVertex(v.ID()));
        machine.getConnections().forEach(v -> graph.addEdge(
                v.getStartingState().ID(),
                v.getResultState().ID(),
                new ConditionEdge(v.getExpressionDescription())
        ));

        JGraphXAdapter<String, ConditionEdge> graphAdapter = new JGraphXAdapter<>(graph);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.setOrientation(graphSettings.getOrientation());
        layout.setIntraCellSpacing(graphSettings.getHorizontalSpacing());
        layout.setInterRankCellSpacing(graphSettings.getVerticalSpacing());
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(
                graphAdapter,
                null,
                graphSettings.getGraphScale(),
                graphSettings.getBackgroundColor(),
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
