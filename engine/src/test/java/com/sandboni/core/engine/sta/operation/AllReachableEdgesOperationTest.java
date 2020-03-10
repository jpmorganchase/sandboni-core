package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.junit.Test;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static com.sandboni.core.engine.sta.graph.LinkType.*;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static org.junit.Assert.*;

public class AllReachableEdgesOperationTest extends GraphOperationsTest {

    @Test
    public void getAllReachableEdges() {
        Set<Edge> allReachableEdges = graphOperations.getAllReachableEdges();
        assertNotNull(allReachableEdges);
        assertEquals(10, allReachableEdges.size());

        // sort first to ensure order of elements is the same across implementations
        TreeSet<Edge> sorted = new TreeSet<>(Comparator.comparing(edge -> ("" + edge.getSource() + edge.getTarget() + edge.getLinkType())));
        sorted.addAll(allReachableEdges);
        Iterator<Edge> iterator = sorted.iterator();

        Edge edge1 = iterator.next();
        assertEquals(METHOD_CALL, edge1.getLinkType());
        assertEquals(modified, edge1.getSource());
        assertEquals(caller, edge1.getTarget());

        Edge edge2 = iterator.next();
        assertEquals(METHOD_CALL, edge2.getLinkType());
        assertEquals(modified, edge2.getSource());
        assertEquals(relatedWithReflection, edge2.getTarget());

        Edge edge3 = iterator.next();
        assertEquals(METHOD_CALL, edge3.getLinkType());
        assertEquals(modified, edge3.getSource());
        assertEquals(relatedWithoutReflection, edge3.getTarget());

        Edge edge4 = iterator.next();
        assertEquals(CUCUMBER_TEST, edge4.getLinkType());
        assertEquals(modified, edge4.getSource());
        assertEquals(affectedCucumberTest, edge4.getTarget());

        Edge edge5 = iterator.next();
        assertEquals(METHOD_CALL, edge5.getLinkType());
        assertEquals(caller, edge5.getSource());
        assertEquals(callerTest, edge5.getTarget());

        Edge edge6 = iterator.next();
        assertEquals(ENTRY_POINT, edge6.getLinkType());
        assertEquals(callerTest, edge6.getSource());
        assertEquals(START_VERTEX, edge6.getTarget());

        Edge edge7 = iterator.next();
        assertEquals(ENTRY_POINT, edge7.getLinkType());
        assertEquals(relatedWithReflection, edge7.getSource());
        assertEquals(START_VERTEX, edge7.getTarget());

        Edge edge8 = iterator.next();
        assertEquals(ENTRY_POINT, edge8.getLinkType());
        assertEquals(relatedWithoutReflection, edge8.getSource());
        assertEquals(START_VERTEX, edge8.getTarget());

        Edge edge9 = iterator.next();
        assertEquals(ENTRY_POINT, edge9.getLinkType());
        assertEquals(affectedCucumberTest, edge9.getSource());
        assertEquals(START_VERTEX, edge9.getTarget());

        Edge edge10 = iterator.next();
        assertEquals(EXIT_POINT, edge10.getLinkType());
        assertEquals(END_VERTEX, edge10.getSource());
        assertEquals(modified, edge10.getTarget());
    }

    @Test
    public void emptyIfGraphDoesntContainStartVertex() {
        builder.getGraph().removeVertex(START_VERTEX);
        AllReachableEdgesOperation allReachableEdgesOperation = new AllReachableEdgesOperation();
        SetResult<Edge> result = allReachableEdgesOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void emptyIfGraphDoesntContainEndVertex() {
        builder.getGraph().removeVertex(END_VERTEX);
        AllReachableEdgesOperation allReachableEdgesOperation = new AllReachableEdgesOperation();
        SetResult<Edge> result = allReachableEdgesOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }


    @Test
    public void execute() {
        AllReachableEdgesOperation allReachableEdgesOperation = new AllReachableEdgesOperation();
        SetResult<Edge> result = allReachableEdgesOperation.execute(builder.getGraph());
        Set<Edge> allReachableEdges = result.get();
        assertNotNull(allReachableEdges);
        assertEquals(10, allReachableEdges.size());

        // sort first to ensure order of elements is the same across implementations
        TreeSet<Edge> sorted = new TreeSet<>(Comparator.comparing(edge -> ("" + edge.getSource() + edge.getTarget() + edge.getLinkType())));
        sorted.addAll(allReachableEdges);
        Iterator<Edge> iterator = sorted.iterator();

        Edge edge1 = iterator.next();
        assertEquals(METHOD_CALL, edge1.getLinkType());
        assertEquals(new Vertex.Builder("ClassA", "coveredMethod()").build(), edge1.getSource());
        assertEquals(new Vertex.Builder("ClassB", "callerMethod()").build(), edge1.getTarget());

        Edge edge2 = iterator.next();
        assertEquals(METHOD_CALL, edge2.getLinkType());
        assertEquals(new Vertex.Builder("ClassA", "coveredMethod()").build(), edge2.getSource());
        assertEquals(new TestVertex.Builder("ClassRTest", "testRelatedWithReflectionCall()").build(), edge2.getTarget());

        Edge edge3 = iterator.next();
        assertEquals(METHOD_CALL, edge3.getLinkType());
        assertEquals(new Vertex.Builder("ClassA", "coveredMethod()").build(), edge3.getSource());
        assertEquals(new TestVertex.Builder("ClassRTest", "testRelatedWithoutReflectionCall()").build(), edge3.getTarget());

        Edge edge4 = iterator.next();
        assertEquals(CUCUMBER_TEST, edge4.getLinkType());
        assertEquals(new Vertex.Builder("ClassA", "coveredMethod()").build(), edge4.getSource());
        assertEquals(new CucumberVertex.Builder("featureFile", "scenario2").markAffected(true).build(), edge4.getTarget());

        Edge edge5 = iterator.next();
        assertEquals(METHOD_CALL, edge5.getLinkType());
        assertEquals(new Vertex.Builder("ClassB", "callerMethod()").build(), edge5.getSource());
        assertEquals(new TestVertex.Builder("ClassBTest", "testCallerMethod()").build(), edge5.getTarget());

        Edge edge6 = iterator.next();
        assertEquals(ENTRY_POINT, edge6.getLinkType());
        assertEquals(new TestVertex.Builder("ClassBTest", "testCallerMethod()").build(), edge6.getSource());
        assertEquals(START_VERTEX, edge6.getTarget());

        Edge edge7 = iterator.next();
        assertEquals(ENTRY_POINT, edge7.getLinkType());
        assertEquals(new TestVertex.Builder("ClassRTest", "testRelatedWithReflectionCall()").build(), edge7.getSource());
        assertEquals(START_VERTEX, edge7.getTarget());

        Edge edge8 = iterator.next();
        assertEquals(ENTRY_POINT, edge8.getLinkType());
        assertEquals(new TestVertex.Builder("ClassRTest", "testRelatedWithoutReflectionCall()").build(), edge8.getSource());
        assertEquals(START_VERTEX, edge8.getTarget());

        Edge edge9 = iterator.next();
        assertEquals(ENTRY_POINT, edge9.getLinkType());
        assertEquals(new CucumberVertex.Builder("featureFile", "scenario2").markAffected(true).build(), edge9.getSource());
        assertEquals(START_VERTEX, edge9.getTarget());

        Edge edge10 = iterator.next();
        assertEquals(EXIT_POINT, edge10.getLinkType());
        assertEquals(END_VERTEX, edge10.getSource());
        assertEquals(new Vertex.Builder("ClassA", "coveredMethod()").build(), edge10.getTarget());
    }
}
