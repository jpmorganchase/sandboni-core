package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.connector.JsonConnector;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CUCUMBER_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;
import static org.junit.Assert.assertNotNull;

public class JsonConnectorTest {
    private void addStartLink(Context context, CucumberVertex testVertex) {
        context.addLink(LinkFactory.createInstance(context.getApplicationId(), START_VERTEX, testVertex, LinkType.ENTRY_POINT));
    }

    @Test
    public void testGetConsumers() {
        Context context = new Context(new String[0], new String[0], "", new ChangeScopeImpl());

        CucumberVertex testVertex = new CucumberVertex.Builder("className", "test-name")
                .withFeaturePath("features/className.feature")
                .withScenarioLine(3)
                .build();
        CucumberVertex testVertex2 = new CucumberVertex.Builder("className2", "test-name3")
                .withFeaturePath("features/className2.feature")
                .withScenarioLine(3)
                .build();
        addStartLink(context, testVertex);
        addStartLink(context, testVertex2);

        Connector jsonConnector = new JsonConnector();
        jsonConnector.connect(context);

        assertNotNull(context);
        Link expectedLink1 = LinkFactory.createInstance(context.getApplicationId(), testVertex,
                new CucumberVertex.Builder(CUCUMBER_VERTEX.getActor(), "/rest/unprocess/count/NEW")
                        .build(),
                LinkType.HTTP_REQUEST);
        Link expectedLink2 = LinkFactory.createInstance(context.getApplicationId(), testVertex, new CucumberVertex.Builder(CUCUMBER_VERTEX.getActor(), "/rest/unprocess/count/I").build(),
                LinkType.HTTP_REQUEST);
        Link expectedLink3 = LinkFactory.createInstance(context.getApplicationId(), testVertex, new CucumberVertex.Builder(CUCUMBER_VERTEX.getActor(), "/rest/unprocess/count/DND")
                .build(),
                LinkType.HTTP_REQUEST);
        Link expectedLink4 = LinkFactory.createInstance(context.getApplicationId(), testVertex2, new CucumberVertex.Builder(CUCUMBER_VERTEX.getActor(), "/rest/unprocess/count/ABC")
                .build(),
                LinkType.HTTP_REQUEST);

        assertLinksExist(context, expectedLink1, expectedLink2, expectedLink3, expectedLink4);
    }

    private void assertLinksExist(Context context, Link... links) {
        Arrays.stream(links).forEach(expectedLink -> {
            Optional<Link> result = context.getLinks().filter(l -> l.equals(expectedLink)).findFirst();
            Assert.assertTrue("Missing expected link: " + expectedLink.toString(), result.isPresent());
        });
    }
}
