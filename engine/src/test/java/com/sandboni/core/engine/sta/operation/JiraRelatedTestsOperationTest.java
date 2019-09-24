package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import org.junit.Test;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CONTAINER_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static org.junit.Assert.assertTrue;

public class JiraRelatedTestsOperationTest extends GraphOperationsTest {

    @Test(expected = NullPointerException.class)
    public void nullAllReachableEdges() {
        new JiraRelatedTestsOperation(null, graphOperations.getDisconnectedTests());
    }

    @Test(expected = NullPointerException.class)
    public void nullDisconnectedTests() {
        new JiraRelatedTestsOperation(graphOperations.getAllReachableEdges(), null);
    }

    @Test
    public void emptyIfGraphDoesntContainStartVertex() {
        builder.getGraph().removeVertex(START_VERTEX);
        JiraRelatedTestsOperation jiraRelatedTestsOperation =
                new JiraRelatedTestsOperation(graphOperations.getAllReachableEdges(), graphOperations.getDisconnectedTests());
        SetResult<Edge> result = jiraRelatedTestsOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void emptyIfGraphDoesntContainContainerVertex() {
        builder.getGraph().removeVertex(CONTAINER_VERTEX);
        JiraRelatedTestsOperation jiraRelatedTestsOperation =
                new JiraRelatedTestsOperation(graphOperations.getAllReachableEdges(), graphOperations.getDisconnectedTests());
        SetResult<Edge> result = jiraRelatedTestsOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

}
