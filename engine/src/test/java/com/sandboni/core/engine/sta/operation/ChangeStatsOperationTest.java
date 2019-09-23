package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.result.ChangeStats;
import com.sandboni.core.engine.sta.Builder;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChangeStatsOperationTest {

    private static final String APP_ID = "sandboni-default";
    private final Context context;
    private final Vertex modified;
    private final Vertex modifiedUncovered;
    private final Vertex caller;
    private final Vertex callerTest;
    private final Vertex disconnectedCallerTest;
    private final Vertex appLocation;
    private final Vertex testLocation;
    private final Vertex deeperCallerTest;
    private final Vertex deeperCaller;
    private final Vertex deepCaller;
    private final Vertex modifiedDeep;
    private final Vertex extraModifiedDeep;
    private final Vertex extraDeeperCaller;
    private final Vertex extraDeepCaller;
    private final Vertex extraBottomCallerTest;
    private final Vertex extraBottomCaller;
    private Builder builder;
    private GraphOperations graphOperations;

    public ChangeStatsOperationTest() {
        context = new Context(new String[]{}, new String[]{}, "", new ChangeScopeImpl());

        modified = new Vertex.Builder("ModifiedClass", "modifiedMethod()").build();
        modifiedUncovered = new Vertex.Builder("ModifiedClass", "modifiedUncoveredMethod()").build();

        caller = new Vertex.Builder("CallerClass", "callerMethod()").build();

        callerTest = new TestVertex.Builder("CallerClassTest", "testCallerMethod()", "").build();
        disconnectedCallerTest = new TestVertex.Builder("CallerClassTest", "testDisconnectedCallerMethod()", "").build();

        modifiedDeep = new Vertex.Builder("ModifiedDeepClass", "modifiedDeepMethod()").build();
        deepCaller = new Vertex.Builder("DeepCallerClass", "deepCallerMethod()").build();
        deeperCaller = new Vertex.Builder("DeeperCallerClass", "deeperCallerMethod()").build();
        deeperCallerTest = new TestVertex.Builder("DeeperCallerClassTest", "testDeeperCallerMethod()", "").build();

        extraModifiedDeep = new Vertex.Builder("ExtraModifiedDeepClass", "extraModifiedDeepMethod()").build();
        extraDeepCaller = new Vertex.Builder("ExtraDeepCallerClass", "extraDeepCallerMethod()").build();
        extraDeeperCaller = new Vertex.Builder("ExtraDeeperCallerClass", "extraDeeperCallerMethod()").build();
        extraBottomCallerTest = new TestVertex.Builder("ExtraBottomCallerClassTest", "testExtraBottomCallerMethod()", "").build();
        extraBottomCaller = new Vertex.Builder("ExtraBottomCallerClass", "extraBottomCallerMethod()").build();

        appLocation = new Vertex.Builder("applicationModule", "contain").markSpecial().build();
        testLocation = new Vertex.Builder("testModule", "contain").markSpecial().build();
    }

    @Before
    public void setUp() {

        context.addLink(LinkFactory.createInstance(APP_ID, caller, modified, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, callerTest, caller, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, callerTest, LinkType.ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, disconnectedCallerTest, LinkType.ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, modified, END_VERTEX, LinkType.EXIT_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, modifiedUncovered, END_VERTEX, LinkType.EXIT_POINT));

        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, deeperCallerTest, LinkType.ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, deeperCallerTest, deeperCaller, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, deeperCaller, deepCaller, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, deepCaller, modifiedDeep, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, modifiedDeep, END_VERTEX, LinkType.EXIT_POINT));

        context.addLink(LinkFactory.createInstance(APP_ID, START_VERTEX, extraBottomCallerTest, LinkType.ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(APP_ID, extraBottomCallerTest, extraBottomCaller, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, extraBottomCaller, extraDeeperCaller, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, extraDeeperCaller, extraDeepCaller, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, extraDeepCaller, extraModifiedDeep, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(APP_ID, extraModifiedDeep, END_VERTEX, LinkType.EXIT_POINT));

        builder = new Builder(context);
        graphOperations = new GraphOperations(builder.getGraph(), context);

    }

    @Test
    public void testGetChangeWeight() {
        Map<Vertex, ChangeStats> changeStats = graphOperations.getChangeStats();

        assertNotNull(changeStats);

        assertEquals("Change size", 4, changeStats.size());

        assertEquals("Uncovered Weight value", 0, changeStats.get(modifiedUncovered).getImpactedCodeElements());
        assertEquals("Uncovered Density value", 0, changeStats.get(modifiedUncovered).getRelatedTests());
        assertEquals("Uncovered Percentage Weight value", 0, changeStats.get(modifiedUncovered).getImpactedCodeElementsPercent(), 0.0);
        assertEquals("Uncovered Percentage Density value", 0, changeStats.get(modifiedUncovered).getRelatedTestsPercent(), 0.0);

        assertEquals("Highest Weight value", 4, changeStats.get(extraModifiedDeep).getImpactedCodeElements());
        assertEquals("Highest Density value", 1, changeStats.get(extraModifiedDeep).getRelatedTests());
        assertEquals("Highest Percentage Weight value", 0.2353, changeStats.get(extraModifiedDeep).getImpactedCodeElementsPercent(), 0.0001);
        assertEquals("Highest Percentage Density value", 0.25, changeStats.get(extraModifiedDeep).getRelatedTestsPercent(), 0.0001);

        assertEquals("Higher Weight value", 3, changeStats.get(modifiedDeep).getImpactedCodeElements());
        assertEquals("Higher Density value", 1, changeStats.get(modifiedDeep).getRelatedTests());
        assertEquals("Higher Percentage Weight value", 0.1764, changeStats.get(modifiedDeep).getImpactedCodeElementsPercent(), 0.0001);
        assertEquals("Higher Percentage Density value", 0.25, changeStats.get(modifiedDeep).getRelatedTestsPercent(), 0.0001);

        assertEquals("Lowest Weight value", 2, changeStats.get(modified).getImpactedCodeElements());
        assertEquals("Lowest Density value", 1, changeStats.get(modified).getRelatedTests());
        assertEquals("Lowest Percentage Weight value", 0.1176, changeStats.get(modified).getImpactedCodeElementsPercent(), 0.0001);
        assertEquals("Lowest Percentage Density value", 0.25, changeStats.get(modified).getRelatedTestsPercent(), 0.0001);

        long highestWeight = changeStats.get(extraModifiedDeep).getImpactedCodeElements();
        long higherWeight = changeStats.get(modifiedDeep).getImpactedCodeElements();
        long lowestWeight = changeStats.get(modified).getImpactedCodeElements();
        assertEquals("Highest weight", 4, highestWeight);
        Assert.assertTrue("Highest weight", highestWeight > higherWeight);
        Assert.assertTrue("Higher weight", higherWeight > lowestWeight);
        Assert.assertTrue("Lowest weight", lowestWeight > 0.0);
    }

    @Test(expected = NullPointerException.class)
    public void nullAllTests() {
        new ChangeStatsOperation(graphOperations.getChanges(), null);
    }

    @Test(expected = NullPointerException.class)
    public void nullChanges() {
        new ChangeStatsOperation(null, graphOperations.getAllTests());
    }
}