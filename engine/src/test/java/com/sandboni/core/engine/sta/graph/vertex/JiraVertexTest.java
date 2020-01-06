package com.sandboni.core.engine.sta.graph.vertex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class JiraVertexTest {

    @Test
    public void testEqualsSymmetricWithSameClass() {
        JiraVertex testVertex1 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        JiraVertex testVertex2 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        assertEquals(testVertex1, testVertex2);
        assertEquals(testVertex2, testVertex1);

        JiraVertex testVertexNotEqual = new JiraVertex.Builder("com.actor.2", "action.2").withRevisionId("revisionId").markSpecial().build();
        assertNotEquals(testVertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, testVertex1);
    }

    @Test
    public void testEqualsSymmetricWithParentClass() {
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        JiraVertex testVertex2 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        assertNotEquals(vertex1, testVertex2);
        assertNotEquals(testVertex2, vertex1);

        JiraVertex testVertexNotEqual = new JiraVertex.Builder("com.actor.2", "action.2").withRevisionId("revisionId").markSpecial().build();
        assertNotEquals(vertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, vertex1);
    }

    @Test
    public void testEqualsTransitiveWithSameClass() {
        // If testVertex1.equals(testVertex2) == true and testVertex2.equals(testVertex3) == true
        // then testVertex1.equals(testVertex3) == true
        JiraVertex testVertex1 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        JiraVertex testVertex2 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        JiraVertex testVertex3 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        assertEquals(testVertex1, testVertex2);
        assertEquals(testVertex2, testVertex3);
        assertEquals(testVertex1, testVertex3);

        assertEquals(testVertex2, testVertex1);
        assertEquals(testVertex3, testVertex2);
        assertEquals(testVertex3, testVertex1);
    }

    @Test
    public void testEqualsTransitiveWithParentClass() {
        // If vertex1.equals(testVertex2) == true and testVertex2.equals(testVertex3) == true
        // then vertex1.equals(testVertex3) == true
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        JiraVertex testVertex2 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        JiraVertex testVertex3 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        assertNotEquals(vertex1, testVertex2);
        assertEquals(testVertex2, testVertex3);
        assertNotEquals(vertex1, testVertex3);

        assertNotEquals(testVertex2, vertex1);
        assertEquals(testVertex3, testVertex2);
        assertNotEquals(testVertex3, vertex1);
    }

    @Test
    public void testEqualsConsistentWithSameClass() {
        //equal objects remain equal and unequal objects remain unequal
        JiraVertex testVertex1 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        JiraVertex testVertex2 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        assertEquals(testVertex1, testVertex2);
        assertEquals(testVertex2, testVertex1);

        JiraVertex testVertexNotEqual = new JiraVertex.Builder("com.actor.2", "action.2").withRevisionId("revisionId").markSpecial().build();
        assertNotEquals(testVertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, testVertex1);
    }

    @Test
    public void testEqualsConsistentWithParentClass() {
        //equal objects remain equal and unequal objects remain unequal
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        JiraVertex testVertex2 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        assertNotEquals(vertex1, testVertex2);
        assertNotEquals(testVertex2, vertex1);

        JiraVertex testVertexNotEqual = new JiraVertex.Builder("com.actor.2", "action.2").withRevisionId("revisionId").markSpecial().build();
        assertNotEquals(vertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, vertex1);
    }

    @Test
    public void testHashCodeEqualsWithSameClass() {
        JiraVertex testVertex1 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();
        JiraVertex testVertex2 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();

        int vertex1HashCode = testVertex1.hashCode();
        int vertex2HashCode = testVertex2.hashCode();
        assertEquals(testVertex1, testVertex2);
        assertEquals(vertex1HashCode, vertex2HashCode);

        assertEquals(vertex1HashCode, testVertex1.hashCode());
        assertEquals(vertex2HashCode, testVertex2.hashCode());
    }

    @Test
    public void testHashCodeNotEqualsWithParentClass() {
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        JiraVertex testVertex2 = new JiraVertex.Builder("com.actor", "action").withRevisionId("revisionId").markSpecial().build();

        int vertex1HashCode = vertex1.hashCode();
        int vertex2HashCode = testVertex2.hashCode();
        assertNotEquals(vertex1, testVertex2);
        assertNotEquals(vertex1HashCode, vertex2HashCode);

        assertEquals(vertex1HashCode, vertex1.hashCode());
        assertEquals(vertex2HashCode, testVertex2.hashCode());
    }
}
