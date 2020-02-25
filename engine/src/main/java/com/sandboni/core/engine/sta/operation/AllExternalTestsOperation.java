package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
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
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), "execute", "ALL").start();
        MapResult<String, Set<String>> stringSetMapResult = new MapResult<>(allTests.stream()
                .collect(toMapActorAction));
        swAll.stop();
        return stringSetMapResult;
    }
}
