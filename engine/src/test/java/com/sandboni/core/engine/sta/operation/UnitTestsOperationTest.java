package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnitTestsOperationTest extends GraphOperationsTest  {
    @Test
    public void testExecute() {
        UnitTestsOperation includedTestOperation = new UnitTestsOperation(graphOperations.getAllTests(), false);
        SetResult<TestVertex> result = includedTestOperation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(3, result.get().size());
    }

    @Test
    public void testExecuteWithExternal() {
        UnitTestsOperation includedTestOperation = new UnitTestsOperation(graphOperations.getAllTests(), true);
        SetResult<TestVertex> result = includedTestOperation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(0, result.get().size());
    }
}
