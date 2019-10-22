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

import java.util.Collections;

import static com.sandboni.core.engine.sta.graph.LinkType.ENTRY_POINT;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;


public abstract class GraphOperationsTest {

    protected static final String APP_ID = "sandboni-default";

    protected final Context context;
    protected final Vertex modified;
    protected final Vertex modifiedUncovered;
    protected final Vertex caller;
    protected final Vertex callerTest;
    protected final Vertex disconnectedCallerTest;
    protected final Vertex appLocation;
    protected final Vertex testLocation;
    protected final CucumberVertex cucumberTest;
    protected final CucumberVertex affectedCucumberTest;
    protected final Vertex includeTest;

    protected GraphOperations graphOperations;
    protected Builder builder;

    public GraphOperationsTest() {
        context = new Context(Collections.emptySet(), Collections.emptySet(), "", new ChangeScopeImpl());

        modified = new Vertex.Builder("ClassA", "coveredMethod()").build();
        modifiedUncovered = new Vertex.Builder("ClassA", "uncoveredMethod()").build();

        caller = new Vertex.Builder("ClassB", "callerMethod()").build();

        callerTest = new TestVertex.Builder("ClassBTest", "testCallerMethod()").build();
        disconnectedCallerTest = new TestVertex.Builder("ClassBTest", "testDisconnectedFromCallerMethod()").build();

        appLocation = new Vertex.Builder("applicationModule", "contain").markSpecial().build();
        testLocation = new Vertex.Builder("testModule", "contain").markSpecial().build();

        cucumberTest = new CucumberVertex.Builder("featureFile", "scenario1").build();
        affectedCucumberTest = new CucumberVertex.Builder("featureFile", "scenario2").markAffected(true).build();

        includeTest = new TestVertex.Builder("MustRunMethodTest", "testTwo()").withIncluded(true).build();
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
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, affectedCucumberTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, affectedCucumberTest, modified, LinkType.CUCUMBER_TEST));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, includeTest, ENTRY_POINT));

        builder = new Builder(context);

        graphOperations = new GraphOperations(builder.getGraph(), context);
    }
}
