package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        context = new Context(new String[]{}, new String[]{}, "", new ChangeScopeImpl());

        modified = new Vertex.Builder("SomeClass", "someMethod()").build();
        modifiedUncovered = new Vertex.Builder("SomeClass", "someUncoveredMethod()").build();

        caller = new Vertex.Builder("SomeOtherClass", "someOtherMethod()").build();

        callerTest = new TestVertex.Builder("SomeOtherClassTest", "testSomeOtherMethod()",null).build();
        disconnectedCallerTest = new TestVertex.Builder("SomeOtherClassTest", "testDisconnectedFromSomeOtherMethod()", null).build();
    }

    @Before
    public void setUp() {

        context.addLink(LinkFactory.createInstance(caller, modified, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(callerTest, caller, LinkType.METHOD_CALL));
        context.addLink(LinkFactory.createInstance(VertexInitTypes.START_VERTEX, callerTest, LinkType.ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(VertexInitTypes.START_VERTEX, disconnectedCallerTest, LinkType.ENTRY_POINT));
        context.addLink(LinkFactory.createInstance(modified, VertexInitTypes.END_VERTEX, LinkType.EXIT_POINT));
        context.addLink(LinkFactory.createInstance(modifiedUncovered, VertexInitTypes.END_VERTEX, LinkType.EXIT_POINT));

        builder = new Builder(context);
    }

    @Test
    public void testGetEntryPoints() {
        Set<Vertex> result = builder.getEntryPoints().collect(Collectors.toSet());
        assertEquals(1, result.size());
        assertTrue(result.contains(callerTest));
    }

    @Test
    public void testGetDisconnectedTests() {
        Set<Vertex> result = builder.getDisconnectedEntryPoints().collect(Collectors.toSet());
        assertNotEquals(0, result.size());
    }

    @Test
    public void testGetExitPoints() {
        Set<Vertex> result = builder.getExitPoints().collect(Collectors.toSet());
        assertEquals(2, result.size());
        assertTrue(result.contains(modified));
        assertTrue(result.contains(modifiedUncovered));
    }

    @Test
    public void testGetUnreachableExitPoints() {
        Set<Vertex> result = builder.getUnreachableExitPoints().collect(Collectors.toSet());
        assertEquals(1, result.size());
        assertTrue(result.contains(modifiedUncovered));
    }

    //todo remove comment!!
//todo ***********************
//    @TEST
//    public void testGetDisconnectedEntryPoints() {
//        Set<Vertex> result = builder.getDisconnectedEntryPoints().collect(Collectors.toSet());
//        assertEquals(1, result.size());
//        assertTrue(result.contains(disconnectedCallerTest));
//    }

    @Test
    public void testGetAllAffected() {
        Set<Vertex> result = builder.getAllAffected().collect(Collectors.toSet());
        assertEquals(3, result.size());
        assertTrue(result.contains(modified));
        assertTrue(result.contains(caller));
        assertTrue(result.contains(callerTest));
    }



    @Test
    public void testGetReachableLineNumberCount() {
        int lineCount = builder.getReachableLineNumberCount();
        assertEquals(0, lineCount);
    }

    @Test
    public void testGetJiraList() {
        Stream<String> jiraSet = builder.getJiraList();
        assertNotNull(jiraSet);
    }

    @Test
    public void testGetAllEntryPoints() {
        Stream<TestVertex> all = builder.getAllEntryPoints();
        assertNotNull(all);
    }
}