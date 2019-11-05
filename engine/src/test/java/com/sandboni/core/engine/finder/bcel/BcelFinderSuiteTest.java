package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.finder.bcel.visitors.TestClassVisitor;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Before;
import org.junit.Test;

import static com.sandboni.core.engine.MockChangeDetector.PACKAGE_NAME;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;

public class BcelFinderSuiteTest extends FinderTestBase {

    @Before
    public void setUp() {
        super.initializeContext();
    }

    private void testVisitor(Link[] expectedLinks, ClassVisitor... visitors) {
        Finder f = new BcelFinder(visitors);
        f.findSafe(context);
        assertLinksExist(expectedLinks);
    }

    private void testTestClassVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new TestClassVisitor());
    }

    @Test
    public void testTestSuiteIsDetected() {
        TestVertex tv1 = new TestVertex.Builder(PACKAGE_NAME + ".SuiteTestClass1", "print()", null).build();
        Link expectedLink1 = newLink(START_VERTEX, tv1, LinkType.ENTRY_POINT);
        TestVertex tv2 = new TestVertex.Builder(PACKAGE_NAME + ".SuiteTestClass2", "print()", null).build();
        Link expectedLink2 = newLink(START_VERTEX, tv2, LinkType.ENTRY_POINT);
        TestVertex tv3 = new TestVertex.Builder(PACKAGE_NAME + ".SuiteTestClass3", "print()", null).build();
        Link expectedLink3 = newLink(START_VERTEX, tv3, LinkType.ENTRY_POINT);

        testTestClassVisitor(expectedLink1, expectedLink2, expectedLink3);
    }

}
