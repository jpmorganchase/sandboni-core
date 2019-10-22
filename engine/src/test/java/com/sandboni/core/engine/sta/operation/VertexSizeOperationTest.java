package com.sandboni.core.engine.sta.operation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VertexSizeOperationTest extends GraphOperationsTest {
    @Test
    public void testExecute() {
        VertexSizeOperation operation = new VertexSizeOperation(graphOperations.getAllTests());
        LongResult result = operation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(Long.valueOf(5), result.get());
    }
}
