package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class DisconnectedTestsOperationTest extends GraphOperationsTest {

    @Test
    public void getDisconnectedTests() {
        Set<? extends Vertex> result = graphOperations.getDisconnectedTests();
        assertEquals(5, result.size());
        assertTrue(result.contains(disconnectedCallerTest));
        assertTrue(result.contains(cucumberTest));
    }

    @Test
    public void execute() {
        DisconnectedTestsOperation disconnectedTestsOperation =
                new DisconnectedTestsOperation(graphOperations.getAllTests(), graphOperations.getRelatedTests(), context.getSrcLocations());
        SetResult<TestVertex> result = disconnectedTestsOperation.execute(builder.getGraph());
        Set<TestVertex> disconnectedTests = result.get();
        assertNotNull(disconnectedTests);
        assertEquals(5, disconnectedTests.size());
        assertTrue(disconnectedTests.contains(new TestVertex.Builder("ClassBTest", "testDisconnectedFromCallerMethod()").build()));
        assertTrue(disconnectedTests.contains(new CucumberVertex.Builder("featureFile", "scenario1").build()));
    }

    @Test(expected = NullPointerException.class)
    public void nullAllTests() {
        new DisconnectedTestsOperation(null, graphOperations.getRelatedTests(), context.getSrcLocations());
    }

    @Test(expected = NullPointerException.class)
    public void nullRelatedTests() {
        new DisconnectedTestsOperation(graphOperations.getAllTests(), null, context.getSrcLocations());
    }

    @Test(expected = NullPointerException.class)
    public void nullSrcLocationsPoints() {
        new DisconnectedTestsOperation(graphOperations.getAllTests(), graphOperations.getRelatedTests(), null);
    }
}
