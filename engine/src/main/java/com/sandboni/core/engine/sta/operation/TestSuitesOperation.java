package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestSuiteVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.*;

public class TestSuitesOperation extends AbstractGraphOperation<SetResult<TestVertex>> {

    private final Set<TestVertex> testsToCheck;

    TestSuitesOperation(Set<TestVertex> relatedTests, Set<TestVertex> disconnectedTests) {
        Objects.requireNonNull(relatedTests, INVALID_ARGUMENT);
        Set<TestVertex> allTests = Stream.concat(relatedTests.stream(), disconnectedTests.stream()).collect(Collectors.toSet());
        this.testsToCheck = new HashSet<>(allTests);
    }

    @Override
    public SetResult<TestVertex> execute(Graph<Vertex, Edge> graph) {
        return new SetResult<>(emptyIfFalse(graph.containsVertex(TEST_SUITE_VERTEX) && graph.containsVertex(START_VERTEX) && graph.containsVertex(END_VERTEX),
                () -> testsToCheck.stream().flatMap(v -> graph.edgesOf(v).stream().filter(e -> LinkType.TEST_SUITE.equals(e.getLinkType()))).map(e -> (TestSuiteVertex)e.getTarget())
        ));
    }

}
