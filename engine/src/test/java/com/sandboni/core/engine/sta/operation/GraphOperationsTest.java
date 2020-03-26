package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.Builder;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Before;

import static com.sandboni.core.engine.sta.graph.LinkType.ENTRY_POINT;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.*;


public abstract class GraphOperationsTest {

    protected static final String APP_ID = "sandboni-default";

    protected final Context context;
    protected final Vertex modified;
    protected final Vertex modifiedUncovered;
    protected final Vertex caller;
    protected final Vertex unModifiedSrc;
    protected final Vertex callerTest;
    protected final Vertex disconnectedCallerTest;
    protected final Vertex appLocation;
    protected final Vertex testLocation;
    protected final CucumberVertex cucumberTest;
    protected final CucumberVertex externalCucumberTest;
    protected final CucumberVertex affectedCucumberTest;
    protected final Vertex alwaysRunTest;
    protected final TestVertex runnerTest;
    protected final TestVertex externalUnitTest;

    protected final TestVertex relatedWithReflection;
    protected final TestVertex relatedWithoutReflection;
    protected final TestVertex disconnectedWithReflection;
    protected final TestVertex unRelatedWithReflection;
    protected final TestVertex classRReflectionDummy;

    protected GraphOperations graphOperations;
    protected Builder builder;

    public GraphOperationsTest() {
        context = new Context(new String[0], new String[0], "", new ChangeScopeImpl(), null);

        modified = new Vertex.Builder("ClassA", "coveredMethod()").build();
        modifiedUncovered = new Vertex.Builder("ClassA", "uncoveredMethod()").build();

        caller = new Vertex.Builder("ClassB", "callerMethod()").build();

        unModifiedSrc = new Vertex.Builder("ClassC", "someUnModifiedMethod()", "ClassC").build();

        callerTest = new TestVertex.Builder("ClassBTest", "testCallerMethod()").build();
        externalUnitTest = new TestVertex.Builder("ClassBTest123", "testCallerMethod123()").markAsExternalLocation().build();
        disconnectedCallerTest = new TestVertex.Builder("ClassBTest", "testDisconnectedFromCallerMethod()").build();

        appLocation = new Vertex.Builder("applicationModule", "contain").markSpecial().build();
        testLocation = new Vertex.Builder("testModule", "contain").markSpecial().build();

        cucumberTest = new CucumberVertex.Builder("featureFile", "scenario1").build();

        externalCucumberTest = new CucumberVertex.Builder("featureFile123", "scenario123").markAsExternalLocation().build();
        affectedCucumberTest = new CucumberVertex.Builder("featureFile", "scenario2").markAffected(true).build();

        alwaysRunTest = new TestVertex.Builder("AlwaysRunMethodTest", "testTwo()").withAlwaysRun(true).build();

        runnerTest = new TestVertex.Builder("com.sandboni.core.engine.scenario.CucumberRunner", "runWith", context.getCurrentLocation()).withRunWithOptions("src/test/resources/features/").build();

        relatedWithReflection = new TestVertex.Builder("ClassRTest", "testRelatedWithReflectionCall()").build();
        relatedWithoutReflection = new TestVertex.Builder("ClassRTest", "testRelatedWithoutReflectionCall()").build();
        disconnectedWithReflection = new TestVertex.Builder("ClassRTest", "testDisconnectedWithReflectionCall()").build();
        unRelatedWithReflection = new TestVertex.Builder("ClassRTest", "testUnRelatedWithReflectionCall()").build();
        classRReflectionDummy = new TestVertex.Builder("ClassRTest", "reflection ref (test)", context.getCurrentLocation()).build();
    }

    @Before
    public void setUp() {
        context.addLink(LinkFactory.createInstance(APP_ID, caller, modified, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, callerTest, caller, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, callerTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, disconnectedCallerTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, modified, END_VERTEX, LinkType.EXIT_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, modifiedUncovered, END_VERTEX, LinkType.EXIT_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, cucumberTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, externalUnitTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, externalCucumberTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, affectedCucumberTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, affectedCucumberTest, modified, LinkType.CUCUMBER_TEST));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, alwaysRunTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, runnerTest, CUCUMBER_RUNNER_VERTEX, LinkType.CUCUMBER_RUNNER));
        //Reflection:
        context.addLink(LinkFactory.createInstance(APP_ID, classRReflectionDummy, REFLECTION_CALL_VERTEX, LinkType.REFLECTION_CALL_TEST));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, disconnectedWithReflection, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, relatedWithReflection, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, relatedWithoutReflection, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, relatedWithReflection, modified, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, relatedWithoutReflection, modified, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, unRelatedWithReflection, unModifiedSrc, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, unRelatedWithReflection, ENTRY_POINT));

        builder = new Builder(context);

        graphOperations = new GraphOperations(builder.getGraph(), context);
    }
}
