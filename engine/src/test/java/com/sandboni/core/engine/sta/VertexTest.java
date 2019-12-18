package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.graph.vertex.JiraVertex;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
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
        assertEquals(vertex2, vertex);
    }

    @Test
    public void testHashCode() {
        assertTrue(vertex.hashCode() != 0);
    }

    @Test
    public void testToString() {
        assertEquals("com.actor/action", vertex.toString());
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
        assertEquals(Arrays.asList(3,4,5), v.getLineNumbers());
    }

    @Test
    public void testShortenName() {
        Vertex v = new Vertex.Builder("actor", "action").build();
        assertEquals("name", v.shortenName("name"));

        assertEquals("null", v.shortenName(null));
    }
}