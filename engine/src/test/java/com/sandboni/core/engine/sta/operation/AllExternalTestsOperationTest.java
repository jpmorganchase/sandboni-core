package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class AllExternalTestsOperationTest extends GraphOperationsTest {

    @Test
    public void getAllExternalUnitTests() {
        Set<TestVertex> allExternalTests = graphOperations.getAllExternalUnitTests();
        assertNotNull(allExternalTests);
        assertEquals(3, allExternalTests.size());
        assertTrue(allExternalTests.stream().anyMatch(c -> c.getActor().equals("ClassBTest") && c.getAction().equals("testCallerMethod()")));
        assertTrue(allExternalTests.stream().anyMatch(c -> c.getActor().equals("ClassBTest") && c.getAction().equals("testDisconnectedFromCallerMethod()")));
    }

    @Test
    public void getAllExternalCucumberTests() {
        Set<CucumberVertex> allExternalTests = graphOperations.getAllExternalCucumberTests();
        assertNotNull(allExternalTests);
        assertEquals(2, allExternalTests.size());
        assertTrue(allExternalTests.stream().anyMatch(c -> c.getActor().equals("featureFile") && c.getAction().equals("scenario2")));
        assertTrue(allExternalTests.stream().anyMatch(c -> c.getActor().equals("featureFile") && c.getAction().equals("scenario1")));
    }

    @Test
    public void execute() {
        AllExternalTestsOperation allExternalTestsOperation = new AllExternalTestsOperation(graphOperations.getAllTests());
        MapResult<String, Set<String>> allExternalTestsResult = allExternalTestsOperation.execute(builder.getGraph());
        Map<String, Set<String>> allExternalTests = allExternalTestsResult.get();

        assertNotNull(allExternalTests);
        assertEquals(3, allExternalTests.size());
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
