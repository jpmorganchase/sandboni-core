package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CucumberTestsOperationTest extends GraphOperationsTest {
    @Test
    public void testExecute() {
        CucumberTestsOperation operation = new CucumberTestsOperation(graphOperations.getAllTests());
        SetResult<CucumberVertex> result = operation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(2, result.get().size());
    }

    @Test
    public void testExecuteWithExternal() {
        CucumberTestsOperation operation = new CucumberTestsOperation(graphOperations.getAllTests(), true);
        SetResult<CucumberVertex> result = operation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(0, result.get().size());
    }
}
