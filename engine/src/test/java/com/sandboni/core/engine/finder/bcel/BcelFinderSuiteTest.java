package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.Application;
import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.contract.ChangeDetector;
import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.finder.bcel.visitors.TestClassVisitor;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.scm.scope.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;

public class BcelFinderSuiteTest extends FinderTestBase {

    private static final String TEST_PACKAGE = "com.sandboni.core.engine.scenario";
    private static final Logger log = LoggerFactory.getLogger(BcelFinderSuiteTest.class);

    @Before
    public void setUp() {
        super.initializeContext(new MockForSuiteChangeDetector().getChanges("1", "2"));
    }

    private void testVisitor(Link[] expectedLinks, ClassVisitor... visitors) {
        Finder f = new BcelFinder(visitors);
        f.findSafe(context);
        context.getLinks().forEach(l -> log.info(String.format("Link: %s; caller isSpecial: %s, callee isSpecial: %s", l.toString(), l.getCaller().isSpecial(), l.getCallee().isSpecial())));
        assertLinksExist(expectedLinks);
    }

    private void testTestClassVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new TestClassVisitor());
    }

    @Test
    public void testTestSuiteIsDetected() {
        TestVertex tv1 = new TestVertex.Builder(TEST_PACKAGE + ".SuiteTestClass1", "print()", null).build();
        Link expectedLink1 = newLink(START_VERTEX, tv1, LinkType.ENTRY_POINT);
        TestVertex tv2 = new TestVertex.Builder(TEST_PACKAGE + ".SuiteTestClass2", "print()", null).build();
        Link expectedLink2 = newLink(START_VERTEX, tv2, LinkType.ENTRY_POINT);
        TestVertex tv3 = new TestVertex.Builder(TEST_PACKAGE + ".SuiteTestClass3", "print()", null).build();
        Link expectedLink3 = newLink(START_VERTEX, tv3, LinkType.ENTRY_POINT);

        testTestClassVisitor(expectedLink1, expectedLink2, expectedLink3);
    }

    private static class MockForSuiteChangeDetector implements ChangeDetector {

        @Override
        public ChangeScope<Change> getChanges(String fromChangeId, String toChangeId) {
            ChangeScope<Change> changeScope = new ChangeScopeImpl();
            changeScope.addChange(new SCMChange(TEST_PACKAGE.replace('.', File.separatorChar) + File.separatorChar + "SuiteTestClass1.java",
                    IntStream.range(8, 10).boxed().collect(Collectors.toSet()), ChangeType.MODIFY));
            changeScope.addChange(new SCMChange(TEST_PACKAGE.replace('.', File.separatorChar) + File.separatorChar + "SuiteTestClass2.java",
                    IntStream.range(3, 5).boxed().collect(Collectors.toSet()), ChangeType.MODIFY));
            changeScope.addChange(new SCMChange(TEST_PACKAGE.replace('.', File.separatorChar) + File.separatorChar + "SuiteTestClass3.java",
                    IntStream.range(4, 8).boxed().collect(Collectors.toSet()), ChangeType.MODIFY));
            return changeScope;
        }
    }

}
