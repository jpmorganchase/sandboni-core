package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.JiraVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
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
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), "execute", "ALL").start();
        final String format = "%s | %s | %s";
        SetResult<String> stringSetResult = new SetResult<>(jiraRelatedTests.stream()
                .map(e -> String.format(format,
                        e.getTarget().getAction(),
                        ((JiraVertex) e.getTarget()).getDate(),
                        ((JiraVertex) e.getTarget()).getRevisionId()))
                .collect(Collectors.toSet()));
        swAll.stop();
        return stringSetResult;
    }
}
