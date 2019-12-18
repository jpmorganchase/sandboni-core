package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.finder.bcel.visitors.CallerClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.ImplementingClassVisitor;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.junit.Before;
import org.junit.Test;

import static com.sandboni.core.engine.MockChangeDetector.PACKAGE_NAME;

public class CachedBcelFinderTest extends FinderTestBase {
    private static final String CALLER_ACTOR_VERTEX = PACKAGE_NAME + ".Caller";

    private void testVisitor(Link[] expectedLinks, ClassVisitor... visitors) {
        Finder f = new BcelFinder(visitors);
        f.findSafe(context);

        assertLinksExist(expectedLinks);
    }

    @Before
    public void setUp() {
        super.initializeContext();
    }

    private void testCallerVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new CallerClassVisitor());
    }

    private void testImplementingVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new ImplementingClassVisitor());
    }

    @Test
    public void testExplicitCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "explicitCall()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "doStuff()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testExplicitStaticCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "explicitStaticCall()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "doStuffStatic()").build(),
                LinkType.STATIC_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testInterfaceCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "interfaceCall(" + PACKAGE_NAME + ".DoOtherStuff)").build(),
                new Vertex.Builder(PACKAGE_NAME + ".DoOtherStuff", "doStuffViaInterface()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testDefaultInterfaceCall() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "interfaceDefaultCall(" + PACKAGE_NAME + ".DoOtherStuff)").build(),
                new Vertex.Builder(PACKAGE_NAME + ".DoOtherStuff", "doStuffDefault()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testThreadRunCall() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "threadRunReference()").build(),
                new Vertex.Builder("java.lang.Runnable", "run()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testInterfaceImpl() {
        Link expectedLink1 = newLink(new Vertex.Builder(PACKAGE_NAME + ".DoOtherStuff", "doStuffViaInterface()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "doStuffViaInterface()").build(),
                LinkType.INTERFACE_IMPL);
        Link expectedLink2 = newLink(new Vertex.Builder(PACKAGE_NAME + ".DoStuffBase", "doStuffViaInterface()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "doStuffViaInterface()").build(),
                LinkType.INTERFACE_IMPL);
        testImplementingVisitor(expectedLink1, expectedLink2);
    }

    @Test
    public void testGenericReference() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "genericReference(java.util.List)").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "toString()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testFieldImplGet() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "instanceFieldReferenceGet()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "value").build(),
                LinkType.FIELD_GET);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testFieldImplPut() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "instanceFieldReferencePut()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "value").build(),
                LinkType.FIELD_PUT);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testStaticImplGet() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "staticFieldReferenceGet()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "valueStatic").build(),
                LinkType.STATIC_GET);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testStaticImplPut() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "staticFieldReferencePut()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "valueStatic").build(),
                LinkType.STATIC_PUT);
        testCallerVisitor(expectedLink);
    }
}
