package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class AllRelatedNodesOperationTest extends GraphOperationsTest {

    @Test
    public void getAllRelatedNodes() {
        Set<Vertex> result = graphOperations.getAllRelatedNodes();
        assertEquals(3, result.size());
        assertTrue(result.contains(modified));
        assertTrue(result.contains(caller));
        assertTrue(result.contains(callerTest));
    }

    @Test(expected = NullPointerException.class)
    public void nullParameters() {
        new AllRelatedNodesOperation(null);
    }

    @Test
    public void execute() {
        AllRelatedNodesOperation allRelatedNodesOperation = new AllRelatedNodesOperation(graphOperations.getAllReachableEdges());
        SetResult<Vertex> result = allRelatedNodesOperation.execute(builder.getGraph());
        Set<Vertex> allRelatedNodes = result.get();
        assertNotNull(allRelatedNodes);
        assertEquals(3, allRelatedNodes.size());
        assertTrue(allRelatedNodes.contains(new Vertex.Builder("ClassA", "coveredMethod()").build()));
        assertTrue(allRelatedNodes.contains(new Vertex.Builder("ClassB", "callerMethod()").build()));
        assertTrue(allRelatedNodes.contains(new TestVertex.Builder("ClassBTest", "testCallerMethod()").build()));
    }

}
