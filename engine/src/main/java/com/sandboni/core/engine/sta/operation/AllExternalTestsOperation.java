package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AllExternalTestsOperation extends AbstractGraphOperation<MapResult<String, Set<String>>> {

    private final Set<TestVertex> allTests;

    AllExternalTestsOperation(Set<TestVertex> allTests) {
        Objects.requireNonNull(allTests, INVALID_ARGUMENT);
        this.allTests = new HashSet<>(allTests);
    }

    @Override
    public MapResult<String, Set<String>> execute(Graph<Vertex, Edge> graph) {
        return new MapResult<>(allTests.stream()
                .collect(toMapActorAction));
    }
}
