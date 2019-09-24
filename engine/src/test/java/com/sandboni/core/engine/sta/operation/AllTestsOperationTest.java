package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import java.util.Set;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static org.junit.Assert.*;

public class AllTestsOperationTest extends GraphOperationsTest {

    @Test
    public void getAllTests() {
        Set<TestVertex> allTests = graphOperations.getAllTests();
        assertNotNull(allTests);
        assertEquals(4, allTests.size());
        assertTrue(allTests.contains(callerTest));
        assertTrue(allTests.contains(disconnectedCallerTest));
        assertTrue(allTests.contains(cucumberTest));
        assertTrue(allTests.contains(affectedCucumberTest));
    }

    @Test
    public void emptyIfGraphDoesntContainStartVertex() {
        builder.getGraph().removeVertex(START_VERTEX);
        AllTestsOperation allTestsOperation = new AllTestsOperation();
        SetResult<TestVertex> result = allTestsOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void execute() {
        AllTestsOperation allTestsOperation = new AllTestsOperation();
        SetResult<TestVertex> result = allTestsOperation.execute(builder.getGraph());
        Set<TestVertex> allTests = result.get();
        assertNotNull(allTests);
        assertEquals(4, allTests.size());
        assertTrue(allTests.contains(new TestVertex.Builder("ClassBTest", "testCallerMethod()").build()));
        assertTrue(allTests.contains(new TestVertex.Builder("ClassBTest", "testDisconnectedFromCallerMethod()").build()));
        assertTrue(allTests.contains(new CucumberVertex.Builder("featureFile", "scenario1").build()));
        assertTrue(allTests.contains(new CucumberVertex.Builder("featureFile", "scenario2").build()));
    }
}
