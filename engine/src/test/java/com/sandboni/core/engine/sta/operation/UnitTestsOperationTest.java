package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnitTestsOperationTest extends GraphOperationsTest  {
    @Test
    public void testExecute() {
        UnitTestsOperation operation = new UnitTestsOperation(graphOperations.getAllTests());
        SetResult<TestVertex> result = operation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(3, result.get().size());
    }

    @Test
    public void testExecuteWithExternal() {
        UnitTestsOperation operation = new UnitTestsOperation(graphOperations.getAllTests(), true);
        SetResult<TestVertex> result = operation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(1, result.get().size());
    }
}
