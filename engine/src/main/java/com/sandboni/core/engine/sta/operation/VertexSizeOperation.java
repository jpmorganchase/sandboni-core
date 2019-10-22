package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.Objects;
import java.util.Set;

public class VertexSizeOperation extends AbstractGraphOperation<LongResult> {
    private final Set<TestVertex> tests;

    VertexSizeOperation(Set<TestVertex> tests) {
        Objects.requireNonNull(tests, INVALID_ARGUMENT);
        this.tests = tests;
    }

    @Override
    public LongResult execute(Graph<Vertex, Edge> graph) {
        return new LongResult((long) tests.size());
    }
}
