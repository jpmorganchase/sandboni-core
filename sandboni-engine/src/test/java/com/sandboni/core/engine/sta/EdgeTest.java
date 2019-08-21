package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.LinkType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EdgeTest {

    private Edge edge;

    @Before
    public void setUp() {
        edge = new Edge();
        edge.setLinkType(LinkType.METHOD_CALL);
    }

    @Test
    public void testGetLinkType() {
        assertEquals(LinkType.METHOD_CALL, edge.getLinkType());
    }

    @Test
    public void testToString() {
        assertEquals(LinkType.METHOD_CALL.description(), edge.toString());
    }

    @Test
    public void testEquals() {
        assertTrue(edge.equals(edge));
        assertFalse(edge.equals(null));

        Edge edge2 = new Edge();
        edge2.setLinkType(LinkType.METHOD_CALL);
        assertTrue(edge.equals(edge2));

        Edge edge3 = new Edge();
        edge3.setLinkType(LinkType.HTTP_HANLDER);
        assertFalse(edge.equals(edge3));

    }

    @Test
    public void testHashCode() {
        assertTrue(edge.hashCode() != 0);
    }
}