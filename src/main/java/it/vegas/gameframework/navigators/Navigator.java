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

public class Navigator {

    public static <C extends GameContext> void printMachine(GameState<C> start) {
        printMachine(start, "  ");
    }

    private static <C extends GameContext> void printMachine(GameState<C> start, String indent) {
        System.out.println(indent + start.toString());
        for (GameStateCondition<C> i : start.getNextGameStates()) {

            Navigator.printMachine(i.getResultState(), " " + indent.replace("└─", "  ") + "└─ ");
        }
    }

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

    public static <C extends GameContext> void GenerateMachineGraph(GameState<C> start, Integer branchSpacing, String filename) {
        DefaultDirectedGraph<String, ConditionEdge> graph = new DefaultDirectedGraph<>(ConditionEdge.class);
        graph = getMachineGraph(start, graph);

        JGraphXAdapter<String, ConditionEdge> graphAdapter = new JGraphXAdapter<String, ConditionEdge>(graph);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.setIntraCellSpacing((branchSpacing != null && branchSpacing > 100) ? branchSpacing : 100);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 3, Color.WHITE, true, null);
        File imgFile = new File((!Objects.equals(filename, "")) ? filename + ".png" : "graph.png");
        try {
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <C extends GameContext> DefaultDirectedGraph<String, ConditionEdge> getMachineGraph(GameState<C> start, DefaultDirectedGraph<String, ConditionEdge> graph) {
        if (!graph.containsVertex(start.getName())) {
            graph.addVertex(start.getName());
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
