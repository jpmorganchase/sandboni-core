package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.connector.TestSuiteConnector;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestSuiteVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static com.sandboni.core.engine.MockChangeDetector.PACKAGE_NAME;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.TEST_SUITE_VERTEX;

public class TestSuiteConnectorTest {
    private TestVertex tv1 = new TestVertex.Builder(PACKAGE_NAME + ".SuiteTestClass1", "print()", null).build();
    private TestVertex tv2 = new TestVertex.Builder(PACKAGE_NAME + ".SuiteTestClass2", "print()", null).build();
    private TestVertex tv3 = new TestVertex.Builder(PACKAGE_NAME + ".SuiteTestClass3", "print()", null).build();

    private TestVertex tsv = new TestSuiteVertex.Builder(PACKAGE_NAME + ".TestSuiteExample", "", null).build();
    Vertex tsv1 = new Vertex.Builder(PACKAGE_NAME + ".SuiteTestClass1", "").build();
    Vertex tsv2 = new Vertex.Builder(PACKAGE_NAME + ".SuiteTestClass2", "").build();
    Vertex tsv3 = new Vertex.Builder(PACKAGE_NAME + ".SuiteTestClass3", "").build();

    private Context setupContext() {
        Context context = new Context(new String[0], new String[0], "", new ChangeScopeImpl());

        Link link1 = LinkFactory.createInstance(context.getApplicationId(), START_VERTEX, tv1, LinkType.ENTRY_POINT);
        Link link2 = LinkFactory.createInstance(context.getApplicationId(), START_VERTEX, tv2, LinkType.ENTRY_POINT);
        Link link3 = LinkFactory.createInstance(context.getApplicationId(), START_VERTEX, tv3, LinkType.ENTRY_POINT);
        Link tsLink1 = LinkFactory.createInstance(context.getApplicationId(), TEST_SUITE_VERTEX, tsv, LinkType.TEST_SUITE);
        Link ts2tv1Link =  LinkFactory.createInstance(context.getApplicationId(), tsv, tsv1, LinkType.TEST_SUITE);
        Link ts2tv2Link =  LinkFactory.createInstance(context.getApplicationId(), tsv, tsv2, LinkType.TEST_SUITE);
        Link ts2tv3Link =  LinkFactory.createInstance(context.getApplicationId(), tsv, tsv3, LinkType.TEST_SUITE);

        context.addLinks(link1, link2, link3, tsLink1, ts2tv1Link, ts2tv2Link, ts2tv3Link);
        return context;
    }

    @Test
    public void testConnect() {
        Context context = setupContext();

        Connector connector = new TestSuiteConnector();

        Assert.assertTrue(connector.proceed(context));
        connector.connect(context);

        Optional<Link> ts2tsv1 = context.getLinks().filter(l -> l.getCaller().equals(tsv) && l.getCallee().equals(tv1)).findFirst();
        Assert.assertTrue("Missing expected convention-based link", ts2tsv1.isPresent());

        Optional<Link> ts2tsv2 = context.getLinks().filter(l -> l.getCaller().equals(tsv) && l.getCallee().equals(tv2)).findFirst();
        Assert.assertTrue("Missing expected convention-based link", ts2tsv2.isPresent());

        Optional<Link> ts2tsv3 = context.getLinks().filter(l -> l.getCaller().equals(tsv) && l.getCallee().equals(tv3)).findFirst();
        Assert.assertTrue("Missing expected convention-based link", ts2tsv3.isPresent());
    }

 }
