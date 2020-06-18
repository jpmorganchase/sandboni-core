package com.sandboni.core.engine.finder.explicit;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.MockChangeDetector;
import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.common.ExtensionType;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ExplicitFinderTest extends FinderTestBase {

    public ExplicitFinderTest() {
        super();
        initializeContext();
    }

    @Before
    @Override
    public void initializeContext() {
        super.initializeContext();
    }

    @Test
    public void testGetConsumers() throws JAXBException, IOException {
        Links links = new Links();

        links.location = this.location;
        Link link = new Link();
        link.callee = MockChangeDetector.PACKAGE_NAME + ".Callee";
        link.calleeAction = "calleeAction()";

        link.caller = MockChangeDetector.PACKAGE_NAME + ".Caller";
        link.callerAction = "callerAction()";

        links.link.add(link);
        links.link.add(link);

        File testFile = File.createTempFile("test-", ExtensionType.SANDBONI.type());
        testFile.deleteOnExit();

        JAXBContext jContext = JAXBContext.newInstance(Links.class);
        Marshaller marshaller = jContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(links, new FileWriter(testFile));

        ExplicitFinder explicitFinder = new ExplicitFinder();
        Map<String, ThrowingBiConsumer<File, Context>> consumers = explicitFinder.getConsumers();

        ThrowingBiConsumer<File, Context> consumer = consumers.get(ExtensionType.SANDBONI.type());
        consumer.accept(testFile, context);
        Assert.assertTrue(context.getLinks().count() > 0);

        assertLinksExist(ExplicitFinder.toLink(context.getApplicationId(), link));
    }

    @Test
    public void testManualLink() {

        ExplicitFinder explicitFinder = new ExplicitFinder();
        explicitFinder.findSafe(context);

        Assert.assertTrue(context.getLinks().count() > 0);

        assertLinksExist(newLink(
                new Vertex.Builder(MockChangeDetector.PACKAGE_NAME + ".HttpTest", "testDisconnectedManualMap()").build(),
                new Vertex.Builder(MockChangeDetector.PACKAGE_NAME + ".JavaxController", "updateRequest(java.lang.String)").build(),
                LinkType.MANUAL));
    }
}