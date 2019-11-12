package com.sandboni.core.engine.sta.connector;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestSuiteVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Connects between each suite test class to it's related methods (test vertices)
 */
public class TestSuiteConnector implements Connector {

    @Override
    public void connect(Context context) {
        Set<Vertex> tests = context.getLinks().filter(l -> l.getLinkType() == LinkType.ENTRY_POINT).map(Link::getCallee).collect(Collectors.toSet());
        Set<Vertex> allTestSuiteVertices = context.getLinks().filter(l -> l.getLinkType() == LinkType.TEST_SUITE).map(Link::getCallee).collect(Collectors.toSet());
        // connect test suite to relevant test (if applicable):
        tests.parallelStream().forEach(t -> {
            // check if we have a related suite class
            allTestSuiteVertices.stream().filter(tsv -> tsv instanceof TestSuiteVertex && ((TestSuiteVertex)tsv).getRelatedTestClasses().contains(t.getActor())).forEach(ts -> {
                // create a link: testSuite -> testVertex, type:TEST_SUITE
                context.addLink(LinkFactory.createInstance(context.getApplicationId(), ts, t, LinkType.TEST_SUITE));
            });
        });
    }

    @Override
    public boolean proceed(Context context) {
        return context.isAdoptedLinkType(LinkType.TEST_SUITE);
    }
}