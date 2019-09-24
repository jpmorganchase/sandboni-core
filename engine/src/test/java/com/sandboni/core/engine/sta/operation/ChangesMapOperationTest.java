package com.sandboni.core.engine.sta.operation;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class ChangesMapOperationTest extends GraphOperationsTest {

    @Test
    public void getChanges() {
        Map<String, Set<String>> changesMap = graphOperations.getChangesMap();
        assertNotNull(changesMap);
        assertEquals(1, changesMap.size());
        Set<String> classA = changesMap.get("ClassA");
        assertNotNull(classA);
        assertEquals(2, classA.size());
        assertTrue(classA.contains("coveredMethod()"));
        assertTrue(classA.contains("uncoveredMethod()"));
    }

    @Test(expected = NullPointerException.class)
    public void nullParameters() {
        new ChangesMapOperation(null);
    }

    @Test
    public void execute() {
        ChangesMapOperation changesMapOperation = new ChangesMapOperation(graphOperations.getChanges());
        MapResult<String, Set<String>> result = changesMapOperation.execute(builder.getGraph());
        Map<String, Set<String>> changesMap = result.get();
        assertNotNull(changesMap);
        assertEquals(1, changesMap.size());
        Set<String> classA = changesMap.get("ClassA");
        assertNotNull(classA);
        assertEquals(2, classA.size());
        assertTrue(classA.contains("coveredMethod()"));
        assertTrue(classA.contains("uncoveredMethod()"));
    }
}
