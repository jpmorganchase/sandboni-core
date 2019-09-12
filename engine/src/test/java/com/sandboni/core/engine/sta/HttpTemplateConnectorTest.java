package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.connector.HttpTemplateConnector;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;
import java.util.Optional;

public class HttpTemplateConnectorTest {

    private String sampleVerb =  HttpConsts.getHttpVerb().stream().findFirst().get();

    private Context setupContext(String callerActon, String calleeAction) {
        Context context = new Context(new String[]{}, new String[]{}, "", new ChangeScopeImpl());

        if (Objects.nonNull(callerActon)) {
            context.addLink(LinkFactory.createInstance(
                    new Vertex.Builder( "caller", "callSite").build(),
                    new Vertex.Builder(sampleVerb + " " + HttpConsts.HTTP_LOCALHOST, callerActon)
                            .markSpecial()
                            .build(),
                    LinkType.HTTP_REQUEST));
        }

        if (Objects.nonNull(calleeAction)) {
            context.addLink(LinkFactory.createInstance(
                    new Vertex.Builder(sampleVerb + " " + HttpConsts.HTTP_LOCALHOST, calleeAction)
                            .markSpecial()
                            .build(),
                    new Vertex.Builder("callee", "action").build() ,
                    LinkType.HTTP_HANLDER));
        }
        return context;
    }

    private void testConnect(String callerActon, String calleeAction) {
        Context context = setupContext(callerActon, calleeAction);

        Connector connector = new HttpTemplateConnector();
        connector.connect(context);

        Optional<Link> result = context.getLinks().filter(l -> l.getCaller().getAction().equals(callerActon) && l.getCallee().getAction().equals(calleeAction) && l.getLinkType() == LinkType.HTTP_MAP).findFirst();
        Assert.assertTrue("Missing expected link between :" + callerActon + " and " + calleeAction, result.isPresent());
    }

    @Test
    public void testConnectTemplatizedParameters() {
        testConnect("/segment2/{value}", "/segment2/{param}");
    }

    @Test
    public void testConnectTemplatizedParameterMissing() {
        testConnect("/segment2/", "/segment2/{param}");
    }

    @Test
    public void testConnectHardcodedParams() {
        testConnect("/app/my/111", "/app/my/{sealId}");
    }

    @Test
    public void testConnectUnexpectedPrefix() {
        testConnect("/segment1/segment2/action", "/segment2/action");
    }


    @Test
    public void testIsMatchTrivialCase() {
        HttpTemplateConnector connector = new HttpTemplateConnector();
        Assert.assertFalse(connector.isMatch("/action", "/action"));
    }

    @Test
    public void testIsProceed(){
        Connector connector = new HttpTemplateConnector();
        Assert.assertTrue(connector.proceed(setupContext("/segment2/{value}", "/segment2/{param}")));
    }

    @Test
    public void testShouldNotProceedWhenHandlerIsMissing(){
        Connector connector = new HttpTemplateConnector();
        Assert.assertFalse(connector.proceed(setupContext("/segment2/{value}", null)));
    }

    @Test
    public void testShouldNotProceedWhenRequestIsMissing(){
        Connector connector = new HttpTemplateConnector();
        Assert.assertFalse(connector.proceed(setupContext(null, "/segment2/{param}")));
    }

    @Test
    public void testShouldNotProceedWhenRNoAnnotations(){
        Connector connector = new HttpTemplateConnector();
        Assert.assertFalse(connector.proceed(setupContext(null, null)));
    }


}