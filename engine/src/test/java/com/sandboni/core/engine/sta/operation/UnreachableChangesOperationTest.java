package com.sandboni.core.engine.sta.operation;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class UnreachableChangesOperationTest extends GraphOperationsTest {

    @Test
    public void getUnreachableChanges() {
        Map<String, Set<String>> result = graphOperations.getUnreachableChanges();
        assertEquals(1, result.size());
        assertNotNull(result.get(modifiedUncovered.getActor()));
    }

    @Test
    public void emptyIfGraphDoesntContainStartVertex() {
        builder.getGraph().removeVertex(START_VERTEX);
        UnreachableChangesOperation unreachableChangesOperation = new UnreachableChangesOperation();
        MapResult<String, Set<String>> result = unreachableChangesOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void emptyIfGraphDoesntContainEndVertex() {
        builder.getGraph().removeVertex(END_VERTEX);
        UnreachableChangesOperation unreachableChangesOperation = new UnreachableChangesOperation();
        MapResult<String, Set<String>> result = unreachableChangesOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

}
