package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.result.ChangeStats;
import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static java.util.stream.Collectors.toMap;

public class ChangeStatsOperation extends AbstractGraphOperation<MapResult<Vertex, ChangeStats>> {

    private final Set<Vertex> changes;
    private final Set<TestVertex> allTests;

    public ChangeStatsOperation(Set<Vertex> changes, Set<TestVertex> allTests) {
        Objects.requireNonNull(changes, INVALID_ARGUMENT);
        Objects.requireNonNull(allTests, INVALID_ARGUMENT);
        this.changes = new HashSet<>(changes);
        this.allTests = new HashSet<>(allTests);
    }

    @Override
    public MapResult<Vertex, ChangeStats> execute(Graph<Vertex, Edge> graph) {
        return new MapResult<>(changes.parallelStream()
                .collect(toMap(vertex -> vertex, vertex -> calculateStats(graph, vertex))));
    }

    private ChangeStats calculateStats(Graph<Vertex, Edge> graph, Vertex change) {
        DepthFirstIterator<Vertex, Edge> iterator = new DepthFirstIterator<>(graph, change);
        long sumPath = -1;
        long countTest = 0;
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
            if (!START_VERTEX.equals(vertex)) {
                sumPath++;
                if (graph.edgesOf(vertex).stream().anyMatch(edge -> START_VERTEX.equals(edge.getTarget()))) {
                    countTest++;
                }
            }
        }
        return new ChangeStats(countTest, sumPath,
                allTests.isEmpty() ? 0.0 : Math.round(((double) countTest / (double) allTests.size()) * 10000) / 10000.0,
                graph.edgeSet().isEmpty() ? 0.0 : Math.round(((double) sumPath / (double) graph.edgeSet().size()) * 10000) / 10000.0);
    }
}
