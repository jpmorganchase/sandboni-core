package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;

import java.util.List;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;

public class AllReachableEdgesOperation extends AbstractGraphOperation<SetResult<Edge>> {

    private static final int MAX_PATH_LENGTH = 100;

    AllReachableEdgesOperation() {
    }

    @Override
    public SetResult<Edge> execute(Graph<Vertex, Edge> graph) {
        return new SetResult<>(emptyIfFalse(graph.containsVertex(START_VERTEX) && graph.containsVertex(END_VERTEX),
                () -> {
                    AllDirectedPaths<Vertex, Edge> algorithm = new AllDirectedPaths<>((DirectedGraph<Vertex, Edge>) graph);
                    List<GraphPath<Vertex, Edge>> ways = algorithm.getAllPaths(END_VERTEX, START_VERTEX, true, MAX_PATH_LENGTH);
                    return ways.stream().flatMap(w -> w.getEdgeList().stream()).distinct();
                }));
    }
}
