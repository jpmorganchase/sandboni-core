package com.sandboni.core.engine.sta.executor;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.sta.operation.GraphOperation;
import com.sandboni.core.engine.sta.operation.OperationResult;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sandboni.core.engine.utils.TimeUtils.elapsedTime;

public class OperationExecutor {

    private static final Logger logger = LoggerFactory.getLogger(OperationExecutor.class);

    private final Graph<Vertex, Edge> graph;

    public OperationExecutor(Graph<Vertex, Edge> graph) {
        this.graph = graph;
    }

    public <T> T execute(GraphOperation<? extends OperationResult<T>> operation) {
        long start = System.nanoTime();

        T result = operation.execute(graph).get();

        logger.info("[{}] Operation {} completed in {} milliseconds",
                Thread.currentThread().getName(), operation.getClass().getSimpleName(), elapsedTime(start));
        return result;
    }
}
