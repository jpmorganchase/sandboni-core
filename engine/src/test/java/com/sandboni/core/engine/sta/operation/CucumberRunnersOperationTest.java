package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import static org.junit.Assert.*;

public class CucumberRunnersOperationTest extends GraphOperationsTest  {
    @Test
    public void testExecute() {
        CucumberRunnersOperation includedTestOperation = new CucumberRunnersOperation();
        SetResult<TestVertex> result = includedTestOperation.execute(builder.getGraph());

        assertNotNull(result);
        assertEquals(1, result.get().size());
        assertTrue(result.get().stream().anyMatch(t -> t.getActor().equals("com.sandboni.core.engine.scenario.CucumberRunner") &&
                t.getAction().equals("runWith") && t.getRunWithOptions().equals("src/test/resources/features/")));
    }
}
