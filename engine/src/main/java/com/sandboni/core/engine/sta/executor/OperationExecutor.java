package com.sandboni.core.engine.sta.executor;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.sta.operation.GraphOperation;
import com.sandboni.core.engine.sta.operation.OperationResult;
import org.jgrapht.Graph;

public class OperationExecutor {

    private final Graph<Vertex, Edge> graph;

    public OperationExecutor(Graph<Vertex, Edge> graph) {
        this.graph = graph;
    }

    public <T> T execute(GraphOperation<? extends OperationResult<T>> operation) {
        return operation.execute(graph).get();
    }
}
