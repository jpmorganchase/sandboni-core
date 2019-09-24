package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.junit.Test;

import java.util.Set;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class ChangesOperationTest extends GraphOperationsTest {

    @Test
    public void getChanges() {
        Set<Vertex> changes = graphOperations.getChanges();
        assertNotNull(changes);
        assertEquals(2, changes.size());
        assertTrue(changes.contains(modified));
        assertTrue(changes.contains(modifiedUncovered));
    }

    @Test
    public void emptyIfGraphDoesntContainEndVertex() {
        builder.getGraph().removeVertex(END_VERTEX);
        ChangesOperation changesOperation = new ChangesOperation();
        SetResult<Vertex> result = changesOperation.execute(builder.getGraph());
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void execute() {
        ChangesOperation changesOperation = new ChangesOperation();
        SetResult<Vertex> result = changesOperation.execute(builder.getGraph());
        Set<Vertex> changes = result.get();
        assertNotNull(changes);
        assertEquals(2, changes.size());
        assertTrue(changes.contains(new Vertex.Builder("ClassA", "coveredMethod()").build()));
        assertTrue(changes.contains(new Vertex.Builder("ClassA", "uncoveredMethod()").build()));
    }
}
