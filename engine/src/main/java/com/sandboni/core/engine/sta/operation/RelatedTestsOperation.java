package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.*;

public class RelatedTestsOperation extends AbstractGraphOperation<SetResult<TestVertex>> {

    private final Set<TestVertex> allTests;

    RelatedTestsOperation(Set<TestVertex> allTests) {
        Objects.requireNonNull(allTests, INVALID_ARGUMENT);
        this.allTests = new HashSet<>(allTests);
    }

    @Override
    public SetResult<TestVertex> execute(Graph<Vertex, Edge> graph) {
        ShortestPathAlgorithm<Vertex, Edge> algorithm = new BellmanFordShortestPath<>(graph);
        return new SetResult<>(emptyIfFalse(graph.containsVertex(START_VERTEX) && graph.containsVertex(END_VERTEX),
                () -> allTests.stream()
                        .filter(v -> isAffectedCucumberVertex(v) || algorithm.getPath(END_VERTEX, v) != null)
                .flatMap(v -> handleSuiteVertex(v, graph).stream())
        ));
        // todo: handle dups
    }

    /**
     * replace any test which is part of a suite with it's suite vertex
     */
    private Set<TestVertex> handleSuiteVertex(TestVertex v, Graph<Vertex, Edge> graph) {
        // get all related test suites
        Set<TestVertex> relatedTestSuiteVertices = graph.edgesOf(v).stream().filter(e -> e.getLinkType().equals(LinkType.TEST_SUITE)).map(e -> (TestVertex) e.getTarget()).collect(Collectors.toSet());
        return relatedTestSuiteVertices.isEmpty()? Collections.singleton(v): relatedTestSuiteVertices;
    }

    private boolean isAffectedCucumberVertex(Vertex v) {
        return v instanceof CucumberVertex && ((CucumberVertex) v).isAffected();
    }
}
