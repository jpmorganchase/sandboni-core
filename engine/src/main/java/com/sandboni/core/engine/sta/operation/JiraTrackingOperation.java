package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.JiraVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JiraTrackingOperation extends AbstractGraphOperation<SetResult<String>> {

    private final Set<Edge> jiraRelatedTests;

    JiraTrackingOperation(Set<Edge> jiraRelatedTests) {
        Objects.requireNonNull(jiraRelatedTests, INVALID_ARGUMENT);
        this.jiraRelatedTests = new HashSet<>(jiraRelatedTests);
    }

    @Override
    public SetResult<String> execute(Graph<Vertex, Edge> graph) {
        final String format = "%s | %s | %s";
        return new SetResult<>(jiraRelatedTests.stream()
                .map(e -> String.format(format,
                        e.getTarget().getAction(),
                        ((JiraVertex) e.getTarget()).getDate(),
                        ((JiraVertex) e.getTarget()).getRevisionId()))
                .collect(Collectors.toSet()));
    }
}
