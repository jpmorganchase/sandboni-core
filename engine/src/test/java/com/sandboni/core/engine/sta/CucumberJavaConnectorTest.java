package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.connector.CucumberJavaConnector;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;
import java.util.Optional;

public class CucumberJavaConnectorTest {
    private Context setupContext(String callerActon, String calleeAction) {
        Context context = new Context(new String[]{}, new String[]{}, "", new ChangeScopeImpl());

        if (Objects.nonNull(callerActon)) {
            context.addLink(LinkFactory.createInstance(
                    new Vertex.Builder("cucumber", callerActon).markSpecial().build(),
                    new Vertex.Builder("cucumber", "callee").markSpecial().build(),
                    LinkType.CUCUMBER_SOURCE));
        }

        if (Objects.nonNull(calleeAction)) {
            context.addLink(LinkFactory.createInstance(
                    new Vertex.Builder("cucumber", "caller").markSpecial().build(),
                    new Vertex.Builder("cucumber", calleeAction).markSpecial().build(),
                    LinkType.CUCUMBER_TEST));
        }
        return context;
    }

    private Optional<Link> testConnect(String callerAction, String calleeAction) {
        Context context = setupContext(callerAction, calleeAction);

        Connector connector = new CucumberJavaConnector();
        connector.connect(context);

        return context.getLinks().filter(l ->  l.getLinkType() == LinkType.CUCUMBER_MAP).findFirst();
    }

    @Test
    public void testIsMatchTrivialCase() {
        CucumberJavaConnector connector = new CucumberJavaConnector();
        Assert.assertTrue(connector.isMatch("^the generated report message header should have no approvals$", "the generated report message header should have no approvals"));
    }

    @Test
    public void testIsMatchWrongCase() {
        CucumberJavaConnector connector = new CucumberJavaConnector();
        Assert.assertFalse(connector.isMatch("^the generated report message header should have no approvals$", "the generated report should have no approvals"));
    }

    @Test
    public void testConnectCucumberMap() {
        Assert.assertTrue(testConnect("^the generated report message header should have no approvals$", "the generated report message header should have no approvals").isPresent());
    }

    @Test
    public void testConnectWrongCucumberMap() {
        Assert.assertFalse(testConnect("^the generated report message header should have approvals$", "the generated report should have no approvals").isPresent());
    }

    @Test
    public void testIsProceed(){
        Connector connector = new CucumberJavaConnector();
        Assert.assertTrue(connector.proceed(setupContext("^the generated report message header should have approvals$", "the generated report should have no approvals")));
    }

    @Test
    public void testShouldNotProceedWhenHandlerIsMissing(){
        Connector connector = new CucumberJavaConnector();
        Assert.assertFalse(connector.proceed(setupContext("^the generated report message header should have approvals$", null)));
    }

    @Test
    public void testShouldNotProceedWhenRequestIsMissing(){
        Connector connector = new CucumberJavaConnector();
        Assert.assertFalse(connector.proceed(setupContext(null, "the generated report should have no approvals")));
    }

    @Test
    public void testShouldNotProceedWhenRNoAnnotations(){
        Connector connector = new CucumberJavaConnector();
        Assert.assertFalse(connector.proceed(setupContext(null, null)));
    }
}
