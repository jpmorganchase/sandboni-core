package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.result.FilterIndicator;
import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.jgrapht.DirectedGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.sandboni.core.engine.sta.graph.LinkType.*;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static org.junit.Assert.*;

public class BuilderTest {

    private final Context context;
    private final Vertex modified;
    private final Vertex modifiedUncovered;
    private final Vertex caller;
    private final Vertex callerTest;
    private final Vertex disconnectedCallerTest;

    private Builder builder;

    public BuilderTest() {
        context = new Context(new String[0], new String[0], "", new ChangeScopeImpl(), null);

        modified = new Vertex.Builder("SomeClass", "someMethod()").build();
        modifiedUncovered = new Vertex.Builder("SomeClass", "someUncoveredMethod()").build();

        caller = new Vertex.Builder("SomeOtherClass", "someOtherMethod()").build();

        callerTest = new TestVertex.Builder("SomeOtherClassTest", "testSomeOtherMethod()",null).build();
        disconnectedCallerTest = new TestVertex.Builder("SomeOtherClassTest", "testDisconnectedFromSomeOtherMethod()", null).build();
    }

    @Before
    public void setUp() {

        context.addLink(LinkFactory.createInstance(context.getApplicationId(), caller, modified, METHOD_CALL));
        context.addLink(LinkFactory.createInstance(context.getApplicationId(), callerTest, caller, METHOD_CALL));
        context.addLink(LinkFactory.createInstance(context.getApplicationId(), START_VERTEX, callerTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(context.getApplicationId(), START_VERTEX, disconnectedCallerTest, ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(context.getApplicationId(), modified, END_VERTEX, EXIT_POINT));
        context.addLink(LinkFactory.createInstance(context.getApplicationId(), modifiedUncovered, END_VERTEX, EXIT_POINT));

        builder = new Builder(context);
    }

    @Test
    public void getGraph() {
        DirectedGraph<Vertex, Edge> graph = builder.getGraph();
        assertNotNull(graph);
        assertEquals(graph, builder.getGraph());
    }

    @Test
    public void getFilterIndicator() {
        assertEquals(FilterIndicator.SELECTIVE, builder.getFilterIndicator());
    }

    @Test
    public void verticesAreIncluded() {
        DirectedGraph<Vertex, Edge> graph = builder.getGraph();
        assertEquals(7, graph.vertexSet().size());
        assertTrue(graph.containsVertex(modified));
        assertTrue(graph.containsVertex(modifiedUncovered));
        assertTrue(graph.containsVertex(caller));
        assertTrue(graph.containsVertex(callerTest));
        assertTrue(graph.containsVertex(disconnectedCallerTest));
        assertTrue(graph.containsVertex(START_VERTEX));
        assertTrue(graph.containsVertex(END_VERTEX));
    }

    @Test
    public void edgesAreIncluded() {
        DirectedGraph<Vertex, Edge> graph = builder.getGraph();
        Set<Edge> edges = graph.edgeSet();
        assertEquals(6, edges.size());

        // sort first to ensure order of elements is the same across implementations
        TreeSet<Edge> sorted = new TreeSet<>(Comparator.comparing(edge -> ("" + edge.getSource() + edge.getTarget() + edge.getLinkType())));
        sorted.addAll(edges);
        Iterator<Edge> iterator = sorted.iterator();

        Edge edge1 = iterator.next();
        assertEquals(METHOD_CALL, edge1.getLinkType());
        assertEquals(modified, edge1.getSource());
        assertEquals(caller, edge1.getTarget());

        Edge edge2 = iterator.next();
        assertEquals(METHOD_CALL, edge2.getLinkType());
        assertEquals(caller, edge2.getSource());
        assertEquals(callerTest, edge2.getTarget());

        Edge edge3 = iterator.next();
        assertEquals(ENTRY_POINT, edge3.getLinkType());
        assertEquals(disconnectedCallerTest, edge3.getSource());
        assertEquals(START_VERTEX, edge3.getTarget());

        Edge edge4 = iterator.next();
        assertEquals(ENTRY_POINT, edge4.getLinkType());
        assertEquals(callerTest, edge4.getSource());
        assertEquals(START_VERTEX, edge4.getTarget());

        Edge edge5 = iterator.next();
        assertEquals(EXIT_POINT, edge5.getLinkType());
        assertEquals(END_VERTEX, edge5.getSource());
        assertEquals(modified, edge5.getTarget());

        Edge edge6 = iterator.next();
        assertEquals(EXIT_POINT, edge6.getLinkType());
        assertEquals(END_VERTEX, edge6.getSource());
        assertEquals(modifiedUncovered, edge6.getTarget());

    }
}