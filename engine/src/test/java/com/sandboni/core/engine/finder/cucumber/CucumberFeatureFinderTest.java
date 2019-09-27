package com.sandboni.core.engine.finder.cucumber;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeType;
import com.sandboni.core.scm.scope.SCMChange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CUCUMBER_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static org.junit.Assert.*;

public class CucumberFeatureFinderTest extends FinderTestBase {
    @Before
    @Override
    public void initializeContext() {
        location = "unknown";
        super.initializeContext();
    }

    @Test
    public void testToLinks() throws IOException {
        String FEATURE_PATH = "target/test-classes/Features/Tagged.feature";
        File file = new File(FEATURE_PATH);
        List<Link> links = CucumberFeatureFinder.toLinks(context, file);
        assertEquals(18, links.size());

        links.forEach(context::addLink);

        CucumberVertex testVertex1 = new CucumberVertex.Builder("Feature Lorem Ipsum", "Where does it")
                .withFeaturePath(file.getAbsolutePath()).withScenarioLine(14).build();
        Link expectedLink1 = newLink(START_VERTEX, testVertex1, LinkType.ENTRY_POINT);

        CucumberVertex testVertex2 = new CucumberVertex.Builder("Feature Lorem Ipsum", "come from?")
                .withFeaturePath((file.getAbsolutePath())).withScenarioLine(22).build();
        Link expectedLink2 = newLink(START_VERTEX, testVertex2, LinkType.ENTRY_POINT);

        CucumberVertex tagVertex1 = new CucumberVertex.Builder(CUCUMBER_VERTEX.getActor(), "TAG-1").build();

        Link expectedLink3 = newLink(testVertex1, tagVertex1, LinkType.CUCUMBER_TEST_TAG);
        Link expectedLink4 = newLink(testVertex2, tagVertex1, LinkType.CUCUMBER_TEST_TAG);

        Assert.assertTrue(testVertex1.isSpecial());
        Assert.assertTrue(testVertex2.isSpecial());
        Assert.assertTrue(tagVertex1.isSpecial());

        assertLinksExist(expectedLink1, expectedLink2, expectedLink3, expectedLink4);
    }

    @Test
    public void testToLinksTpm() throws IOException {

        String FEATURE_PATH = "target/test-classes/Features/TpmFeature.feature";
        File file = new File(FEATURE_PATH);

        List<Link> links = CucumberFeatureFinder.toLinks(context, file);
        assertEquals(9, links.size());


        links.forEach(context::addLink);

        CucumberVertex testVertex1 = new CucumberVertex.Builder("Why do we use it feature", "It is a long established fact that a reader will")
                .withFeaturePath(file.getAbsolutePath()).withScenarioLine(3).build();
        Link expectedLink1 = newLink(START_VERTEX, testVertex1, LinkType.ENTRY_POINT);
        CucumberVertex testVertex2 = new CucumberVertex.Builder("cucumber", "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour").build();
        Link expectedLink2 = newLink(testVertex1, testVertex2, LinkType.CUCUMBER_TEST);
        CucumberVertex testVertex3 = new CucumberVertex.Builder("cucumber", "All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet").build();
        Link expectedLink3 = newLink(testVertex1, testVertex3, LinkType.CUCUMBER_TEST);
        CucumberVertex testVertex4 = new CucumberVertex.Builder("cucumber", "or randomised words which don't look even slightly believable").build();
        Link expectedLink4 = newLink(testVertex1, testVertex4, LinkType.CUCUMBER_TEST);
        CucumberVertex testVertex5 = new CucumberVertex.Builder("cucumber", "If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text").build();
        Link expectedLink5 = newLink(testVertex1, testVertex5, LinkType.CUCUMBER_TEST);

        CucumberVertex testVertex6 = new CucumberVertex.Builder("Why do we use it feature", "The point of using Lorem Ipsum is that it has")
        .withFeaturePath(file.getAbsolutePath()).withScenarioLine(16).build();
        Link expectedLink6 = newLink(START_VERTEX, testVertex6, LinkType.ENTRY_POINT);
        CucumberVertex testVertex7 = new CucumberVertex.Builder("cucumber", "making it look like readable English").build();
        Link expectedLink7 = newLink(testVertex6, testVertex7, LinkType.CUCUMBER_TEST);
        CucumberVertex testVertex8 = new CucumberVertex.Builder("cucumber", "Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy").build();
        Link expectedLink8 = newLink(testVertex6, testVertex8, LinkType.CUCUMBER_TEST);
        CucumberVertex testVertex9 = new CucumberVertex.Builder("cucumber", "Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like)").build();
        Link expectedLink9 = newLink(testVertex6, testVertex9, LinkType.CUCUMBER_TEST);

        assertLinksExist(expectedLink1, expectedLink2, expectedLink3, expectedLink4, expectedLink5, expectedLink6, expectedLink7, expectedLink8, expectedLink9);
    }

    @Test
    public void testToLinksCountLines() throws IOException {
        String filepath = "target/test-classes/Features/TpmFeature.feature";
        List<Link> links = CucumberFeatureFinder.toLinks(context, new File(filepath));
        assertEquals(9, links.size());

        Optional<Vertex> vertex1 = links.stream().filter(l ->
                l.getCaller().getActor().equals("Why do we use it feature") &&
                        l.getCaller().getAction().equals("It is a long established fact that a reader will")).map(Link::getCaller).findFirst();
        assertTrue(vertex1.isPresent());
        assertEquals(new File(filepath).getAbsolutePath(), ((CucumberVertex)vertex1.get()).getFeaturePath());
        assertEquals(3, ((CucumberVertex)vertex1.get()).getScenarioLine());

        Optional<Vertex> vertex2 = links.stream().filter(l ->
                l.getCaller().getActor().equals("Why do we use it feature") &&
                        l.getCaller().getAction().equals("The point of using Lorem Ipsum is that it has")).map(Link::getCaller).findFirst();
        assertTrue(vertex2.isPresent());
        assertEquals(new File(filepath).getAbsolutePath(), ((CucumberVertex)vertex2.get()).getFeaturePath());
        assertEquals(16, ((CucumberVertex)vertex2.get()).getScenarioLine());
    }

    @Test
    public void testToLinksCount() throws IOException {
        List<Link> links = CucumberFeatureFinder.toLinks(context, new File("target/test-classes/Features/TpmFeatureCommented.feature"));
        assertEquals(8, links.size());
    }

    @Test
    public void testFullyCommentedFeature() throws IOException {
        List<Link> links = CucumberFeatureFinder.toLinks(context, new File("target/test-classes/Features/Commented.feature"));
        assertEquals(0, links.size());
    }

    @Test
    public void testAffectedScenarios() throws IOException {
        context.getChangeScope().addChange(new SCMChange("resources/Features/Tagged.feature", new HashSet<>(Arrays.asList(18, 20)), ChangeType.MODIFY));

        List<Link> links = CucumberFeatureFinder.toLinks(context, new File("target/test-classes/Features/Tagged.feature"));

        Optional<Vertex> vertex = links.stream().filter(l ->
                l.getCaller().getActor().equals("Feature Lorem Ipsum") &&
                        l.getCaller().getAction().equals("Where does it")).map(Link::getCaller).findFirst();
        assertTrue(vertex.isPresent());
        assertTrue(((CucumberVertex)vertex.get()).isAffected());

        Optional<Vertex> vertex2 = links.stream().filter(l ->
                l.getCaller().getActor().equals("Feature Lorem Ipsum") &&
                        l.getCaller().getAction().equals("come from?")).map(Link::getCaller).findFirst();
        assertTrue(vertex2.isPresent());
        assertFalse(((CucumberVertex)vertex2.get()).isAffected());
    }
}