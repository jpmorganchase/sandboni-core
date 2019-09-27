package com.sandboni.core.engine.sta.operation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReachableLineNumberCountOperationTest extends GraphOperationsTest {

    @Test
    public void getReachableLineNumberCount() {
        Long lineCount = graphOperations.getReachableLineNumberCount();
        assertEquals(new Long(0), lineCount);
    }

    @Test(expected = NullPointerException.class)
    public void nullAllReachableEdges() {
        new ReachableLineNumberCountOperation(null, graphOperations.getAllTests());
    }

    @Test(expected = NullPointerException.class)
    public void nullAllTests() {
        new ReachableLineNumberCountOperation(graphOperations.getAllReachableEdges(), null);
    }
}
