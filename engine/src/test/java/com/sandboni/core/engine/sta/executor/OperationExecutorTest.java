package com.sandboni.core.engine.sta.executor;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Assert;
import org.junit.Test;

public class OperationExecutorTest {

    @Test
    public void testExecute() {
        DirectedGraph<Vertex, Edge> graph = new DefaultDirectedGraph<>(Edge.class);
        OperationExecutor operationExecutor = new OperationExecutor(graph);
        String result = operationExecutor.execute(graph1 -> {
            Assert.assertEquals("Just validate this method was called", graph, graph1);
            return () -> "Result";
        });
        Assert.assertEquals("Result", result);
    }
}
