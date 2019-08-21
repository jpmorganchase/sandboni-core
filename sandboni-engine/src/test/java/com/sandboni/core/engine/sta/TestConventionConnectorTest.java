package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.connector.TestConventionConnector;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class TestConventionConnectorTest {

    private Vertex test = new Vertex.Builder("package.SomeClassTest", "testSomeMethodForZeros()").build();
    private Vertex code = new Vertex.Builder("package.SomeClass", "someMethod(int, int)").build();
    private Vertex code2 = new Vertex.Builder("package.SomeClass", "someOtherMethod(int, int, int)").build();

    private Context setupContext() {
        Context context = new Context(new String[]{}, new String[]{}, "", new ChangeScopeImpl());

        Link link1 = LinkFactory.createInstance(VertexInitTypes.START_VERTEX, test, LinkType.ENTRY_POINT);
        Link link2 = LinkFactory.createInstance(code, code2, LinkType.METHOD_CALL);

        context.addLinks(link1, link2);
        return context;
    }

    @Test
    public void testConnect() {
        Context context = setupContext();

        Connector connector = new TestConventionConnector();
        connector.connect(context);

        Optional<Link> result = context.getLinks().filter(l -> l.getCaller().equals(test) && l.getCallee().equals(code)).findFirst();
        Assert.assertTrue("Missing expected convention-based link", result.isPresent());
        Assert.assertTrue(connector.proceed(context));
    }
}