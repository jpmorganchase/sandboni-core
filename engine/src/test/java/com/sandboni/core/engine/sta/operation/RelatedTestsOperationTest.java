package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import java.util.Set;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static org.junit.Assert.*;

public class RelatedTestsOperationTest extends GraphOperationsTest {

    @Test
    public void getRelatedTests() {
        Set<TestVertex> result = graphOperations.getRelatedTests();
        assertEquals(2, result.size());
        assertTrue(result.contains(callerTest));
        assertTrue(result.contains(affectedCucumberTest));
    }

    @Test
    public void emptyIfGraphDoesntContainStartVertex() {
        RelatedTestsOperation relatedTestsOperation = new RelatedTestsOperation(graphOperations.getAllTests());
        builder.getGraph().removeVertex(START_VERTEX);
        SetResult<TestVertex> result = relatedTestsOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void emptyIfGraphDoesntContainEndVertex() {
        RelatedTestsOperation relatedTestsOperation = new RelatedTestsOperation(graphOperations.getAllTests());
        builder.getGraph().removeVertex(END_VERTEX);
        SetResult<TestVertex> result = relatedTestsOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void execute() {
        RelatedTestsOperation relatedTestsOperation = new RelatedTestsOperation(graphOperations.getAllTests());
        SetResult<TestVertex> result = relatedTestsOperation.execute(builder.getGraph());
        Set<TestVertex> relatedTests = result.get();
        assertNotNull(relatedTests);
        assertEquals(2, relatedTests.size());
        assertTrue(relatedTests.contains(new TestVertex.Builder("ClassBTest", "testCallerMethod()").build()));
        assertTrue(relatedTests.contains(new CucumberVertex.Builder("featureFile", "scenario2").build()));
    }

    @Test(expected = NullPointerException.class)
    public void nullParameters() {
        new RelatedTestsOperation(null);
    }
}
