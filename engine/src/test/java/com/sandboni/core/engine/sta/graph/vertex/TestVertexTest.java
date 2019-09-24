package com.sandboni.core.engine.sta.graph.vertex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestVertexTest {

    @Test
    public void testEqualsSymmetricWithSameClass() {
        TestVertex testVertex1 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        TestVertex testVertex2 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        assertEquals(testVertex1, testVertex2);
        assertEquals(testVertex2, testVertex1);

        TestVertex testVertexNotEqual = new TestVertex.Builder("com.actor.2", "action.2").markSpecial().build();
        assertNotEquals(testVertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, testVertex1);
    }

    @Test
    public void testEqualsSymmetricWithParentClass() {
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        TestVertex testVertex2 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        assertNotEquals(vertex1, testVertex2);
        assertNotEquals(testVertex2, vertex1);

        TestVertex testVertexNotEqual = new TestVertex.Builder("com.actor.2", "action.2").markSpecial().build();
        assertNotEquals(vertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, vertex1);
    }

    @Test
    public void testEqualsTransitiveWithSameClass() {
        // If testVertex1.equals(testVertex2) == true and testVertex2.equals(testVertex3) == true
        // then testVertex1.equals(testVertex3) == true
        TestVertex testVertex1 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        TestVertex testVertex2 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        TestVertex testVertex3 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
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
        TestVertex testVertex2 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        TestVertex testVertex3 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
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
        TestVertex testVertex1 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        TestVertex testVertex2 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        assertEquals(testVertex1, testVertex2);
        assertEquals(testVertex2, testVertex1);

        // after update
        testVertex1.setFilter("Some filter");
        testVertex2.setFilter("Some other filter");
        assertEquals(testVertex1, testVertex2);
        assertEquals(testVertex2, testVertex1);

        TestVertex testVertexNotEqual = new TestVertex.Builder("com.actor.2", "action.2").markSpecial().build();
        assertNotEquals(testVertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, testVertex1);

        testVertexNotEqual.setFilter("Some filter");
        assertNotEquals(testVertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, testVertex1);
    }

    @Test
    public void testEqualsConsistentWithParentClass() {
        //equal objects remain equal and unequal objects remain unequal
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        TestVertex testVertex2 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        assertNotEquals(vertex1, testVertex2);
        assertNotEquals(testVertex2, vertex1);

        // after update
        vertex1.setFilter("Some filter");
        testVertex2.setFilter("Some other filter");
        assertNotEquals(vertex1, testVertex2);
        assertNotEquals(testVertex2, vertex1);

        TestVertex testVertexNotEqual = new TestVertex.Builder("com.actor.2", "action.2").markSpecial().build();
        assertNotEquals(vertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, vertex1);

        testVertexNotEqual.setFilter("Some filter");
        assertNotEquals(vertex1, testVertexNotEqual);
        assertNotEquals(testVertexNotEqual, vertex1);
    }

    @Test
    public void testHashCodeEqualsWithSameClass() {
        TestVertex testVertex1 = new TestVertex.Builder("com.actor", "action").markSpecial().build();
        TestVertex testVertex2 = new TestVertex.Builder("com.actor", "action").markSpecial().build();

        int vertex1HashCode = testVertex1.hashCode();
        int vertex2HashCode = testVertex2.hashCode();
        assertEquals(testVertex1, testVertex2);
        assertEquals(vertex1HashCode, vertex2HashCode);

        // after update, hash code should remain the same
        testVertex1.setFilter("Some filter");
        testVertex2.setFilter("Some other filter");
        assertEquals(testVertex1, testVertex2);
        assertEquals(vertex1HashCode, testVertex1.hashCode());
        assertEquals(vertex2HashCode, testVertex2.hashCode());
    }

    @Test
    public void testHashCodeNotEqualsWithParentClass() {
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        TestVertex testVertex2 = new TestVertex.Builder("com.actor", "action").markSpecial().build();

        int vertex1HashCode = vertex1.hashCode();
        int vertex2HashCode = testVertex2.hashCode();
        assertNotEquals(vertex1, testVertex2);
        assertNotEquals(vertex1HashCode, vertex2HashCode);

        // after update, hash code should remain the same
        vertex1.setFilter("Some filter");
        testVertex2.setFilter("Some other filter");
        assertNotEquals(vertex1, testVertex2);
        assertEquals(vertex1HashCode, vertex1.hashCode());
        assertEquals(vertex2HashCode, testVertex2.hashCode());
    }
}