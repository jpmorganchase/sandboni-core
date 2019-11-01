package com.sandboni.core.engine.sta.connector;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.utils.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.TEST_SUITE_VERTEX;

/**
 * Connects between each suite test class to it's related methods (test vertices)
 */
public class TestSuiteConnector implements Connector {

    @Override
    public void connect(Context context) {
        List<Link> testSuiteToTestClassLinks = context.getLinks().filter(l -> (l.getLinkType() == LinkType.TEST_SUITE && !l.getCaller().equals(TEST_SUITE_VERTEX))).collect(Collectors.toList());
        Stream<Vertex> testSuitesStream = testSuiteToTestClassLinks.stream().map(Link::getCaller);
        if (!testSuitesStream.findAny().isPresent()) return; // nothing to do here

        Set<Vertex> tests = context.getLinks().filter(l -> l.getLinkType() == LinkType.ENTRY_POINT).map(Link::getCallee).collect(Collectors.toSet());

        // connect test suite to relevant test (if applicable)
        tests.forEach(t -> {
            // check if we have a related suite class
            testSuiteToTestClassLinks.stream().filter(l -> l.getCallee().getActor().equals(t.getActor())).forEach(l -> {
                // create a link: testSuite -> testVertex, type:TEST_SUITE
                context.addLink(LinkFactory.createInstance(context.getApplicationId(), l.getCaller(), t, LinkType.TEST_SUITE));
            });
        });
    }

    @Override
    public boolean proceed(Context context) {
        return true;
    }
}