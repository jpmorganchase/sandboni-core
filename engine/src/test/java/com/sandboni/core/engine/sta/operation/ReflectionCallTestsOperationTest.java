package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import java.util.Set;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.REFLECTION_CALL_VERTEX;
import static org.junit.Assert.*;

public class ReflectionCallTestsOperationTest extends GraphOperationsTest {

    @Test
    public void getReflectionCallTests() {
        Set<TestVertex> result = graphOperations.getReflectionCallTests();
        assertEquals(4, result.size());
        assertTrue(result.contains(relatedWithReflection));
        assertTrue(result.contains(relatedWithoutReflection));
        assertTrue(result.contains(disconnectedWithReflection));
        assertTrue(result.contains(unRelatedWithReflection));
    }

    @Test
    public void emptyIfGraphDoesntContainReflectionCallVertex() {
        ReflectionCallTestsOperation reflectionCallTestsOperation = new ReflectionCallTestsOperation(graphOperations.getAllTests());
        builder.getGraph().removeVertex(REFLECTION_CALL_VERTEX);
        SetResult<TestVertex> result = reflectionCallTestsOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void execute() {
        ReflectionCallTestsOperation reflectionCallTestsOperation = new ReflectionCallTestsOperation(graphOperations.getAllTests());
        SetResult<TestVertex> result = reflectionCallTestsOperation.execute(builder.getGraph());
        Set<TestVertex> reflectionCallTests = result.get();
        assertNotNull(reflectionCallTests);
        assertEquals(4, reflectionCallTests.size());
        assertTrue(reflectionCallTests.contains(new TestVertex.Builder("ClassRTest", "testRelatedWithReflectionCall()").build()));
        assertTrue(reflectionCallTests.contains(new TestVertex.Builder("ClassRTest", "testRelatedWithoutReflectionCall()").build()));
        assertTrue(reflectionCallTests.contains(new TestVertex.Builder("ClassRTest", "testDisconnectedWithReflectionCall()").build()));
        assertTrue(reflectionCallTests.contains(new TestVertex.Builder("ClassRTest", "testUnRelatedWithReflectionCall()").build()));
    }

}
