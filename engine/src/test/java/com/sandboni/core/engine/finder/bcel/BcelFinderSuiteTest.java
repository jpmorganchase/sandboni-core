package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.finder.bcel.visitors.TestClassVisitor;
import com.sandboni.core.engine.scenario.SuiteTestClass1;
import com.sandboni.core.engine.scenario.SuiteTestClass2;
import com.sandboni.core.engine.scenario.SuiteTestClass3;
import com.sandboni.core.engine.scenario.TestSuite1;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestSuiteVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static com.sandboni.core.engine.MockChangeDetector.PACKAGE_NAME;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.TEST_SUITE_VERTEX;

public class BcelFinderSuiteTest extends FinderTestBase {

    @Before
    public void setUp() {
        super.initializeContext();
    }

    private void testVisitor(Link[] expectedLinks, Link[] notExpectedLinks, ClassVisitor... visitors) {
        Finder f = new BcelFinder(visitors);
        f.findSafe(context);
        assertLinksExist(expectedLinks);
        assertLinksNotExist(notExpectedLinks);
    }

    private void testTestClassVisitor(Link[] expectedLinks, Link[] notExpectedLinks) {
        testVisitor(expectedLinks, notExpectedLinks, new TestClassVisitor());
    }

    @Test
    public void testTestSuiteIsDetected() {
        String TEST_CLASS_1 = PACKAGE_NAME + "." + SuiteTestClass1.class.getSimpleName();
        String TEST_CLASS_2 = PACKAGE_NAME + "." + SuiteTestClass2.class.getSimpleName();
        String TEST_CLASS_3 = PACKAGE_NAME + "." + SuiteTestClass3.class.getSimpleName();

        TestVertex tv1 = new TestVertex.Builder(TEST_CLASS_1, "testPrint()", null).build();
        TestVertex tv2 = new TestVertex.Builder(TEST_CLASS_2, "testPrint()", null).build();
        TestVertex tv3 = new TestVertex.Builder(TEST_CLASS_3, "testPrint()", null).build();
        TestVertex tv4 = new TestVertex.Builder(PACKAGE_NAME + "." + TestSuite1.class.getSimpleName(), "testPrint()", null).build();
        TestSuiteVertex tsv = new TestSuiteVertex.Builder(PACKAGE_NAME + "." + TestSuite1.class.getSimpleName(), new HashSet<>(Arrays.asList(TEST_CLASS_1, TEST_CLASS_2, TEST_CLASS_3)), null).build();

        Link expectedLink1 = newLink(START_VERTEX, tv1, LinkType.ENTRY_POINT);
        Link expectedLink2 = newLink(START_VERTEX, tv2, LinkType.ENTRY_POINT);
        Link expectedLink3 = newLink(START_VERTEX, tv3, LinkType.ENTRY_POINT);
        Link notExpectedLink4 = newLink(START_VERTEX, tv4, LinkType.ENTRY_POINT);
        Link tsLink = newLink(TEST_SUITE_VERTEX, tsv, LinkType.TEST_SUITE);

        Link[] expectedLinks = new Link[]{expectedLink1, expectedLink2, expectedLink3, tsLink};
        Link[] notExpectedLinks = new Link[]{notExpectedLink4};
        testTestClassVisitor(expectedLinks, notExpectedLinks);
    }

}
