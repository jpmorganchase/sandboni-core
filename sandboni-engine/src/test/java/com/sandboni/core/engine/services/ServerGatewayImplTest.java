package com.sandboni.core.engine.services;

import com.sandboni.core.engine.exception.GatewayException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ServerGatewayImplTest {
    private static final int SERVER_PORT = 1080;
    private static final String USAGE_URL = "/sandboni/usage";
    private static final String TOKEN_RESPONSE_AS_JSON = "{\"token\":\"6656eb66-6e64-4b6f-b822-89fc75eaa872\",\"status\":\"my-server-response\"}";

    private ClientAndServer mockServer;

    @Before
    public void init() {
        mockServer = ClientAndServer.startClientAndServer(SERVER_PORT);

        mockServer.when(request().withPath(USAGE_URL)).respond(response()
                .withHeaders(new Header("content-type", "application/json")).withBody(TOKEN_RESPONSE_AS_JSON));

    }

    @After
    public void stopMockServer() {
        mockServer.stop();
    }

    @Test
    public void testSendCoverageData() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "val1");
        map.put("key2", "val2");

        ServerGateway serverGateway = new ServerGatewayImpl();
        serverGateway.sendStats(map, "http://localhost:" + SERVER_PORT + "/" + USAGE_URL);
//        assertTrue(true);
    }

    @Test(expected = GatewayException.class)
    public void testInvalidURL() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "val1");
        map.put("key2", "val2");

        ServerGateway serverGateway = new ServerGatewayImpl();
        serverGateway.sendStats(map, null);
    }


}
