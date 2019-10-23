package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CucumberTestsOperationTest extends GraphOperationsTest {
    @Test
    public void testExecute() {
        CucumberTestsOperation includedTestOperation = new CucumberTestsOperation(graphOperations.getAllTests());
        SetResult<CucumberVertex> result = includedTestOperation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(2, result.get().size());
    }

    @Test
    public void testExecuteWithExternal() {
        CucumberTestsOperation includedTestOperation = new CucumberTestsOperation(graphOperations.getAllTests(), true);
        SetResult<CucumberVertex> result = includedTestOperation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(0, result.get().size());
    }
}
