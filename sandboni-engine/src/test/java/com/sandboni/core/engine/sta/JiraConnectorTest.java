package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.connector.JiraConnector;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JiraConnectorTest {
    private Context setupContext() {
        Context context = new Context(new String[]{}, new String[]{}, "", new ChangeScopeImpl());
        context.addLink(LinkFactory.createInstance(
                new Vertex.Builder("cucumber", "callerActon")
                        .withFilePath("com/sandboni/core/scenario/Callee.java")
                        .withLineNumbers(Arrays.asList(1, 2))
                        .build(),
                new Vertex.Builder("cucumber", "callee")
                        .withFilePath("com/sandboni/core/scenario/DoOtherStuff.java")
                        .withLineNumbers(Arrays.asList(3, 4))
                        .build(),
                LinkType.CUCUMBER_SOURCE));

        context.addLink(LinkFactory.createInstance(
                new Vertex.Builder("cucumber", "caller")
                        .withFilePath( "com/sandboni/core/scenario/JavaxController.java")
                        .withLineNumbers(Arrays.asList(5, 6))
                        .build(),
                new Vertex.Builder("cucumber", "calleeAction")
                        .withFilePath("com/sandboni/core/scenario/SpringController.java")
                        .withLineNumbers(Arrays.asList(7, 8))
                        .build(),
                LinkType.CUCUMBER_TEST));
        return context;
    }

    @Test
    public void testIsMatchTrivialCase() {
        Context context = setupContext();
        assertEquals(2, context.getLinks().count());

        String repositoryPath = System.getProperty("user.dir");
        JiraConnector connector = new JiraConnector(repositoryPath);
        connector.connect(context);

        Assert.assertTrue(connector.proceed(context));

        assertTrue(context.getLinks().count() > 1);
    }
}
