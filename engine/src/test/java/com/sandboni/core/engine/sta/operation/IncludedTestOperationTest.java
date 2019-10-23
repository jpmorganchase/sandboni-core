package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import static org.junit.Assert.*;

public class IncludedTestOperationTest extends GraphOperationsTest  {
    @Test
    public void testExecute() {
        IncludedTestOperation includedTestOperation = new IncludedTestOperation(graphOperations.getAllTests());
        SetResult<TestVertex> result = includedTestOperation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(1, result.get().size());
        assertTrue(result.get().stream().anyMatch(t -> t.getActor().equals("MustRunMethodTest") && t.getAction().equals("testTwo()")));
    }
}
