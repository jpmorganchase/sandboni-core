package com.sandboni.core.engine;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import org.junit.Assert;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static com.sandboni.core.engine.MockChangeDetector.PACKAGE_NAME;
import static com.sandboni.core.engine.MockChangeDetector.TEST_LOCATION;

public abstract class FinderTestBase {

    protected String location;
    protected Context context;
    private String filter;

    protected FinderTestBase() {
        location = Paths.get(TEST_LOCATION).normalize().toAbsolutePath().toString();
    }

    protected FinderTestBase(String path, String filter) {
        this.location = Paths.get(path).normalize().toAbsolutePath().toString();
        this.filter = filter;
    }

    protected void initializeContext(ChangeScope<Change> changeScope) {
        this.context = new Context(new HashSet<>(Collections.singletonList(location)), Collections.emptySet(), filter == null ? PACKAGE_NAME : filter, changeScope);
    }

    protected void initializeContext() {
        this.initializeContext(new MockChangeDetector().getChanges("1", "2"));
    }

    protected Link newLink(Vertex caller, Vertex callee, LinkType linkType) {
        return LinkFactory.createInstance(context.getApplicationId(), caller, callee, linkType);
    }

    protected void assertLinksExist(Link... expectedLinks) {
        Arrays.stream(expectedLinks).forEach(expectedLink -> {
            Optional<Link> result = context.getLinks().filter(l -> l.equals(expectedLink)).findFirst();
            Assert.assertTrue("Missing expected link: " + expectedLink.toString(), result.isPresent());
        });
    }
}