package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AlwaysRunTestOperation extends AbstractGraphOperation<SetResult<TestVertex>> {
    private final Set<TestVertex> allTests;

    public AlwaysRunTestOperation(Set<TestVertex> allTests) {
        Objects.requireNonNull(allTests, INVALID_ARGUMENT);
        this.allTests = allTests;
    }

    @Override
    public SetResult<TestVertex> execute(Graph<Vertex, Edge> graph) {
        Set<TestVertex> tests = allTests.stream().filter(TestVertex::isAlwaysRun).collect(Collectors.toSet());
        return new SetResult<>(tests);
    }
}
