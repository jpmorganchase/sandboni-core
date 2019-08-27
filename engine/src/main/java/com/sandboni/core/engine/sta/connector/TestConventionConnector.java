package com.sandboni.core.engine.sta.connector;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.util.Set;
import java.util.stream.Collectors;

public class TestConventionConnector implements Connector {
    @Override
    public void connect(Context context) {
        Set<Vertex> tests = context.getLinks().filter(l -> l.getLinkType() == LinkType.ENTRY_POINT).map(Link::getCallee).collect(Collectors.toSet());
        Set<Vertex> code = context.getLinks().filter(l -> l.getLinkType().isCodeBased()).map(Link::getCaller).collect(Collectors.toSet());

        tests.forEach(t -> code.parallelStream().filter(c -> isMatch(t, c)).findAny()
                .ifPresent(c -> context.addLink(LinkFactory.createInstance(t, c, LinkType.CONVENTION))));
    }

    @Override
    public boolean proceed(Context context) {
        return true;
    }

    // convention here is SomeClassTest(s).testSomeMethodXXX() to SomeClass.someMethod()
    private boolean isMatch(Vertex test, Vertex code) {
        int index = code.getAction().indexOf('(');
        if (code.getAction() == null || index < 0) return false;

        final String testString = "test";
        String expectedTestClass = (code.getActor() + testString).toUpperCase();
        String expectedTestMethod = (testString + code.getAction().substring(0, index)).toUpperCase();

        return (test.getActor().toUpperCase().startsWith(expectedTestClass) &&
                test.getAction().toUpperCase().startsWith(expectedTestMethod));
    }
}