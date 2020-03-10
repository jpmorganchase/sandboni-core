package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.scenario.reflection.src.ReflectionCaller;
import com.sandboni.core.engine.scenario.reflection.test.ReflectionExplicitTest;
import com.sandboni.core.engine.scenario.reflection.test.ReflectionImplicitTest;
import com.sandboni.core.engine.scenario.reflection.test.ReflectionLambdaTest;
import com.sandboni.core.engine.scenario.reflection.test.ReflectionPowerMockTest;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.junit.Before;
import org.junit.Test;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.REFLECTION_CALL_VERTEX;

public class BcelFinderReflectionTest extends FinderTestBase {

    @Before
    public void setUp() {
        super.initializeContext();
    }

    private void testVisitor(Link[] expectedLinks) {
        Finder f = new BcelFinder();
        f.findSafe(context);

        assertLinksExist(expectedLinks);
    }

    private void testCallerVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks);
    }

    @Test
    public void testReflectionCalls() {
        // no test locations on context
        Link expectedLink1 = newLink(new Vertex.Builder(ReflectionCaller.class.getName(), "reflection ref").build(), REFLECTION_CALL_VERTEX, LinkType.REFLECTION_CALL_SRC);
        Link expectedLink2 = newLink(new Vertex.Builder(ReflectionExplicitTest.class.getName(), "reflection ref").build(), REFLECTION_CALL_VERTEX, LinkType.REFLECTION_CALL_SRC);
        Link expectedLink3 = newLink(new Vertex.Builder(ReflectionImplicitTest.class.getName(), "reflection ref").build(), REFLECTION_CALL_VERTEX, LinkType.REFLECTION_CALL_SRC);
        Link expectedLink4 = newLink(new Vertex.Builder(ReflectionLambdaTest.class.getName(), "reflection ref").build(), REFLECTION_CALL_VERTEX, LinkType.REFLECTION_CALL_SRC);
        Link expectedLink5 = newLink(new Vertex.Builder(ReflectionPowerMockTest.class.getName(), "reflection ref").build(), REFLECTION_CALL_VERTEX, LinkType.REFLECTION_CALL_SRC);
        testCallerVisitor(expectedLink1, expectedLink2, expectedLink3, expectedLink4, expectedLink5);
    }

}
