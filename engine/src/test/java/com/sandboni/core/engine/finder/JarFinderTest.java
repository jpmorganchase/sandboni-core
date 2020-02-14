package com.sandboni.core.engine.finder;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.CallerClassVisitor;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

public class JarFinderTest extends FinderTestBase {
    public JarFinderTest() throws FileNotFoundException {
        super(ResourceUtils.getFile("./target/test-classes/jar-finder-1.0.jar").getAbsolutePath(), "sandboni-core");
    }

    private void testVisitor(Link[] expectedLinks, ClassVisitor... visitors) {
        Finder f = new JarFinder();
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

    @Test
    public void testExplicitCall() {
        Link expectedLink1 = newLink(new Vertex.Builder("com.test.App", "main(java.lang.String[])").build(), new Vertex.Builder("com.test.App", "execute()").build(), LinkType.STATIC_CALL);
        Link expectedLink2 = newLink(new Vertex.Builder("com.test.App", "<init>()").build(), new Vertex.Builder("java.lang.Object", "<init>()").build(), LinkType.SPECIAL_CALL);
        testCallerVisitor(expectedLink1, expectedLink2);
    }
}
