package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import static org.junit.Assert.*;

public class AlwaysRunTestOperationTest extends GraphOperationsTest  {
    @Test
    public void testExecute() {
        AlwaysRunTestOperation alwaysRunTestOperation = new AlwaysRunTestOperation(graphOperations.getAllTests());
        SetResult<TestVertex> result = alwaysRunTestOperation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(1, result.get().size());
        assertTrue(result.get().stream().anyMatch(t -> t.getActor().equals("AlwaysRunMethodTest") && t.getAction().equals("testTwo()")));
    }
}
