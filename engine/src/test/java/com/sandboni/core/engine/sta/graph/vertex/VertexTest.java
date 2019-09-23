package com.sandboni.core.engine.sta.graph.vertex;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class VertexTest {
    private Vertex vertex;

    @Before
    public void setUp() {
        vertex = new Vertex.Builder("com.actor", "action").markSpecial().build();
        vertex.setFilter("com");
    }

    @Test
    public void testGetActor() {
        assertEquals("com.actor", vertex.getActor());
    }

    @Test
    public void testGetAction() {
        assertEquals("action", vertex.getAction());
    }

    @Test
    public void testEquals() {
        Vertex vertex2 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        vertex2.setFilter("com");
        assertEquals(vertex2, vertex);
    }


    @Test
    public void testEqualsSame() {
        Vertex v = new Vertex.Builder("com.actor", "action").markSpecial().build();
        assertEquals(v, v);
    }

    @Test
    public void testEqualsFilter() {
        Vertex vertex2 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        vertex2.setFilter("com.something");
        assertEquals(vertex2, vertex);
    }

    @Test
    public void testEqualsReflexive() {
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        assertEquals(vertex1, vertex1);
    }

    @Test
    public void testEqualsSymmetric() {
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        Vertex vertex2 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        assertEquals(vertex1, vertex2);
        assertEquals(vertex2, vertex1);

        Vertex vertexNotEqual = new Vertex.Builder("com.actor.2", "action.2").markSpecial().build();
        assertNotEquals(vertex1, vertexNotEqual);
        assertNotEquals(vertexNotEqual, vertex1);
    }

    @Test
    public void testEqualsTransitive() {
        // If vertex1.equals(vertex2) == true and vertex2.equals(vertex3) == true
        // then vertex1.equals(vertex3) == true
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        Vertex vertex2 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        Vertex vertex3 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        assertEquals(vertex1, vertex2);
        assertEquals(vertex2, vertex3);
        assertEquals(vertex1, vertex3);

        assertEquals(vertex2, vertex1);
        assertEquals(vertex3, vertex2);
        assertEquals(vertex3, vertex1);
    }

    @Test
    public void testEqualsConsistent() {
        //equal objects remain equal and unequal objects remain unequal
        Vertex vertex1 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        Vertex vertex2 = new Vertex.Builder("com.actor", "action").markSpecial().build();
        assertEquals(vertex1, vertex2);
        assertEquals(vertex2, vertex1);

        // after update
        vertex1.setFilter("Some filter");
        vertex2.setFilter("Some other filter");
        assertEquals(vertex1, vertex2);
        assertEquals(vertex2, vertex1);

        Vertex vertexNotEqual = new Vertex.Builder("com.actor.2", "action.2").markSpecial().build();
        assertNotEquals(vertex1, vertexNotEqual);
        assertNotEquals(vertexNotEqual, vertex1);

        vertexNotEqual.setFilter("Some filter");
        assertNotEquals(vertex1, vertexNotEqual);
        assertNotEquals(vertexNotEqual, vertex1);
    }

    @Test
    public void testEqualsNonNull() {
        Vertex vertex1 =  new Vertex.Builder("com.actor", "action").markSpecial().build();
        assertNotEquals(vertex1, null);
    }

    @Test
    public void testHashCodeEquals() {
        Vertex vertex1 =  new Vertex.Builder("com.actor", "action").markSpecial().build();
        Vertex vertex2 =  new Vertex.Builder("com.actor", "action").markSpecial().build();

        int vertex1HashCode = vertex1.hashCode();
        int vertex2HashCode = vertex2.hashCode();
        assertEquals(vertex1, vertex2);
        assertEquals(vertex1HashCode, vertex2HashCode);

        // after update, hash code should remain the same
        vertex1.setFilter("Some filter");
        vertex2.setFilter("Some other filter");
        assertEquals(vertex1, vertex2);
        assertEquals(vertex1.hashCode(), vertex2.hashCode());
        assertEquals(vertex1HashCode, vertex1.hashCode());
        assertEquals(vertex2HashCode, vertex2.hashCode());
    }

    @Test
    public void testHashCode() {
        assertTrue(vertex.hashCode() != 0);
    }

    @Test
    public void testToString() {
        assertEquals("*.actor/action", vertex.toString());
    }

    @Test
    public void testIsSpecial() {
        assertTrue(vertex.isSpecial());
    }

    @Test
    public void testVertex() {
        Date d = new Date();
        JiraVertex v = new JiraVertex.Builder("actor", "action").withDate(d).withRevisionId("revId").build();
        assertEquals("actor", v.getActor());
        assertEquals("action", v.getAction());
        assertTrue(v.isSpecial());
        assertEquals(d, v.getDate());
        assertEquals("revId", v.getRevisionId());
        assertTrue(v.isLineNumbersEmpty());
    }

    @Test
    public void testFeatureVertex() {
        CucumberVertex v = new CucumberVertex.Builder("actor", "action")
                .withFeaturePath("file/path")
                .withScenarioLine(3)
                .build();
        assertEquals(3, v.getScenarioLine());
        assertEquals("file/path", v.getFeaturePath());
        assertNull(v.getFilePath());
        assertNull(v.getFilter());
    }

    @Test
    public void testBasicVertex() {
        Vertex v = new Vertex.Builder("actor", "action").build();
        assertEquals("actor", v.getActor());
        assertEquals("action", v.getAction());
    }

    @Test
    public void testVertexWithLineNumbers() {
        Vertex v = new Vertex.Builder("actor", "action")
                .withFilePath("file/path")
                .withLineNumbers(Arrays.asList(3, 4, 5))
                .build();
        assertEquals("actor", v.getActor());
        assertEquals("action", v.getAction());
        assertEquals("file/path", v.getFilePath());
        assertEquals(Arrays.asList(3, 4, 5), v.getLineNumbers());
    }

    @Test
    public void testShortenName() {
        Vertex v = new Vertex.Builder("actor", "action").build();
        assertEquals("name", v.shortenName("name"));

        v.setFilter("filter");
        assertEquals("null", v.shortenName(null));
    }
}