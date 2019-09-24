package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

import java.util.*;
import java.util.stream.Collectors;

public class DisconnectedTestsOperation extends AbstractGraphOperation<SetResult<TestVertex>> {

    private final Set<TestVertex> allTests;
    private final Set<TestVertex> relatedTests;
    private final Collection<String> sourceLocations;

    DisconnectedTestsOperation(Set<TestVertex> allTests, Set<TestVertex> relatedTests, Collection<String> sourceLocations) {
        Objects.requireNonNull(allTests, INVALID_ARGUMENT);
        Objects.requireNonNull(relatedTests, INVALID_ARGUMENT);
        Objects.requireNonNull(sourceLocations, INVALID_ARGUMENT);
        this.allTests = new HashSet<>(allTests);
        this.relatedTests = new HashSet<>(relatedTests);
        this.sourceLocations = new HashSet<>(sourceLocations);
    }

    @Override
    public SetResult<TestVertex> execute(Graph<Vertex, Edge> graph) {
        DirectedGraph<Vertex, Edge> directedGraph = (DirectedGraph<Vertex, Edge>) graph;
        //getting only the not-ignored and unrelated tests
        Set<TestVertex> notRelatedTests = allTests.parallelStream()
                .filter(t -> !t.isIgnore() && !relatedTests.contains(t)).collect(Collectors.toSet());

        Set<TestVertex> disconnectedTests = new HashSet<>();

        notRelatedTests.forEach(tv -> {
            Deque<Vertex> stack = new LinkedList<>();
            Set<Vertex> visited = new HashSet<>();
            stack.push(tv);
            boolean found = false;
            while (!stack.isEmpty()){
                Vertex v = stack.pop();
                if (!visited.contains(v)) {
                    visited.add(v);
                    if (!v.isSpecial() && Objects.nonNull(v.getLocation()) && sourceLocations.contains(v.getLocation())) {
                        found = true;
                        break;
                    } else {
                        Set<Edge> edges = directedGraph.incomingEdgesOf(v);
                        edges.forEach(e -> stack.push(e.getSource()));
                    }
                }
            }
            if (!found){
                disconnectedTests.add(tv);
            }
        });

        return new SetResult<>(disconnectedTests);
    }
}
