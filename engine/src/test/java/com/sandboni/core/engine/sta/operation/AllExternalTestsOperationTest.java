package com.sandboni.core.engine.sta.operation;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class AllExternalTestsOperationTest extends GraphOperationsTest {

    @Test
    public void getAllExternalTests() {
        Map<String, Set<String>> allExternalTests = graphOperations.getAllExternalTests();
        assertNotNull(allExternalTests);
        assertEquals(2, allExternalTests.size());
        Set<String> classBTest = allExternalTests.get("ClassBTest");
        assertNotNull(classBTest);
        assertEquals(2, classBTest.size());
        assertTrue(classBTest.contains("testDisconnectedFromCallerMethod()"));
        assertTrue(classBTest.contains("testCallerMethod()"));

        Set<String> featureFile = allExternalTests.get("featureFile");
        assertNotNull(featureFile);
        assertEquals(2, featureFile.size());
        assertTrue(featureFile.contains("scenario1"));
        assertTrue(featureFile.contains("scenario2"));
    }

    @Test
    public void execute() {
        AllExternalTestsOperation allExternalTestsOperation = new AllExternalTestsOperation(graphOperations.getAllTests());
        MapResult<String, Set<String>> allExternalTestsResult = allExternalTestsOperation.execute(builder.getGraph());
        Map<String, Set<String>> allExternalTests = allExternalTestsResult.get();

        assertNotNull(allExternalTests);
        assertEquals(2, allExternalTests.size());
        Set<String> classBTest = allExternalTests.get("ClassBTest");
        assertNotNull(classBTest);
        assertEquals(2, classBTest.size());
        assertTrue(classBTest.contains("testDisconnectedFromCallerMethod()"));
        assertTrue(classBTest.contains("testCallerMethod()"));

        Set<String> featureFile = allExternalTests.get("featureFile");
        assertNotNull(featureFile);
        assertEquals(2, featureFile.size());
        assertTrue(featureFile.contains("scenario1"));
        assertTrue(featureFile.contains("scenario2"));
    }

    @Test(expected = NullPointerException.class)
    public void nullParameters() {
        new AllExternalTestsOperation(null);
    }
}
