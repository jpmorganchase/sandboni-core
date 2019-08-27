package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertexHandler;
import com.sandboni.core.engine.sta.graph.vertex.VertexHandler;
import com.sandboni.core.engine.sta.graph.vertex.VertexHandlerFactory;
import org.junit.Assert;
import org.junit.Test;

public class VertexHandlerTest {

    @Test
    public void testFactoryForTest(){
        VertexHandler handler = VertexHandlerFactory.getVertexHandler("test");
        Assert.assertNotNull(handler);
        Assert.assertTrue(handler instanceof TestVertexHandler);
    }

    @Test
    public void testFactoryForDefault(){
        VertexHandler handler = VertexHandlerFactory.getVertexHandler("");
        Assert.assertNotNull(handler);
        Assert.assertFalse(handler instanceof TestVertexHandler);
    }

    @Test
    public void testIsExternalIsCheckedOff() {
        TestVertex tv = new TestVertex.Builder("actor", "action", null).build();
        Assert.assertFalse(tv.isExternalLocation());
        VertexHandlerFactory.getVertexHandler("test").handle(tv, "Location", "*.jar");
        Assert.assertTrue(tv.isExternalLocation());
    }

    @Test
    public void testIsExternalWhenNotJar() {
        TestVertex tv = new TestVertex.Builder("actor", "action", null).build();
        Assert.assertFalse(tv.isExternalLocation());
        VertexHandlerFactory.getVertexHandler("test").handle(tv, "Location", "*.java");
        Assert.assertFalse(tv.isExternalLocation());
    }

    @Test
    public void testIsExternalWhenNotLocation() {
        TestVertex tv = new TestVertex.Builder("actor", "action", null).build();
        Assert.assertFalse(tv.isExternalLocation());
        VertexHandlerFactory.getVertexHandler("test").handle(tv, "abc", "*.java");
        Assert.assertFalse(tv.isExternalLocation());
    }



}
