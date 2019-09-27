package com.sandboni.core.engine.sta.operation;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertNotNull;

public class JiraTrackingOperationTest extends GraphOperationsTest {

    @Test
    public void testGetJiraList() {
        Set<String> jiraSet = graphOperations.getJiraTracking();
        assertNotNull(jiraSet);
    }

    @Test(expected = NullPointerException.class)
    public void nullParams() {
        new JiraTrackingOperation(null);
    }

}
