package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChangesMapOperation extends AbstractGraphOperation<MapResult<String, Set<String>>> {

    private final Set<Vertex> changes;

    ChangesMapOperation(Set<Vertex> changes) {
        Objects.requireNonNull(changes, INVALID_ARGUMENT);
        this.changes = new HashSet<>(changes);
    }

    @Override
    public MapResult<String, Set<String>> execute(Graph<Vertex, Edge> graph) {
        return new MapResult<>(changes.stream().collect(toMapActorAction));
    }
}
