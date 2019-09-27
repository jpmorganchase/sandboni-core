package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.*;

public class JiraRelatedTestsOperation extends AbstractGraphOperation<SetResult<Edge>> {

    private final Set<Edge> allReachableEdges;
    private final Set<TestVertex> disconnectedTests;

    JiraRelatedTestsOperation(Set<Edge> allReachableEdges, Set<TestVertex> disconnectedTests) {
        Objects.requireNonNull(allReachableEdges, INVALID_ARGUMENT);
        Objects.requireNonNull(disconnectedTests, INVALID_ARGUMENT);
        this.allReachableEdges = new HashSet<>(allReachableEdges);
        this.disconnectedTests = new HashSet<>(disconnectedTests);
    }

    @Override
    public SetResult<Edge> execute(Graph<Vertex, Edge> graph) {
        return new SetResult<>(emptyIfFalse(graph.containsVertex(START_VERTEX) && graph.containsVertex(CONTAINER_VERTEX), () ->
                Stream.concat(
                        allReachableEdges.stream()
                                .flatMap(re -> graph.edgesOf(re.getSource()).stream().filter(e -> e.getTarget().getActor().equals(TRACKING_VERTEX.getActor()))),
                        disconnectedTests.stream()
                                .flatMap(v -> graph.edgesOf(v).stream().filter(e -> e.getSource().getActor().equals(TRACKING_VERTEX.getActor()))))
                        .filter(e -> e.getTarget().getActor().equals(TRACKING_VERTEX.getActor()))));
    }
}
