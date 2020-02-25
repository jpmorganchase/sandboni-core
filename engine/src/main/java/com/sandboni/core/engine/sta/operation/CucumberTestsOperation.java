package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
import org.jgrapht.Graph;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CucumberTestsOperation extends AbstractGraphOperation<SetResult<CucumberVertex>> {
    private final Set<TestVertex> tests;
    private final boolean isExternal;

    CucumberTestsOperation(Set<TestVertex> tests) {
        this(tests, false);
    }

    CucumberTestsOperation(Set<TestVertex> tests, boolean isExternal) {
        Objects.requireNonNull(tests, INVALID_ARGUMENT);
        this.tests = tests;
        this.isExternal = isExternal;
    }

    @Override
    public SetResult<CucumberVertex> execute(Graph<Vertex, Edge> graph) {
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), "execute", "ALL").start();
        Set<CucumberVertex> set = tests.stream()
                .filter(v -> v instanceof CucumberVertex && v.isExternalLocation() == isExternal)
                .map(v -> (CucumberVertex) v)
                .collect(Collectors.toSet());
        SetResult<CucumberVertex> cucumberVertexSetResult = new SetResult<>(set);
        swAll.stop();
        return cucumberVertexSetResult;
    }
}
