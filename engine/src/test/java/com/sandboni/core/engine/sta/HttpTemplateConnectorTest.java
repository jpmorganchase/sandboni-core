package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.contract.JsonEntry;
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

import static org.junit.Assert.*;

public class HttpTemplateConnectorTest {

    private String sampleVerb =  HttpConsts.getHttpVerb().stream().findFirst().get();

    private Context setupContext(String callerActon, String calleeAction) {
        Context context = new Context(new String[0], new String[0], "", new ChangeScopeImpl(), null);

        if (Objects.nonNull(callerActon)) {
            context.addLink(LinkFactory.createInstance(
                    context.getApplicationId(),
                    new Vertex.Builder( "caller", "callSite").build(),
                    new Vertex.Builder(sampleVerb + " " + HttpConsts.HTTP_LOCALHOST, callerActon)
                            .markSpecial()
                            .build(),
                    LinkType.HTTP_REQUEST));
        }

        if (Objects.nonNull(calleeAction)) {
            context.addLink(LinkFactory.createInstance(
                    context.getApplicationId(),
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
        Assert.assertTrue(connector.isMatch("/action", "/action"));
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

    @Test
    public void test() {
        Context c = new Context("appId", new String[0], new String[0], new String[0],
                "filter", new ChangeScopeImpl(), null, "src/test/resources/Seloni.json", true);
        JsonEntry[] tests = HttpTemplateConnector.getEntriesFromSeloniFile(c);

        assertEquals(4, tests.length);

        JsonEntry test1 = tests[0];
        assertEquals("className", test1.getClassName());
        assertEquals("I:\\code\\seloni-demo-test\\className.class", test1.getFilepath());
        assertEquals("test-name", test1.getTestName());
        assertEquals("CUCUMBER", test1.getType());
        assertEquals("17:43:19.770", test1.getDate());
        assertNull(test1.getStatus());
        assertFalse(test1.getUrls().isEmpty());
    }
}