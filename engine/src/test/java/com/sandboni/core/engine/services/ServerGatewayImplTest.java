package com.sandboni.core.engine.services;

import com.sandboni.core.engine.exception.GatewayException;
import org.junit.Test;
import org.mockito.Answers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class ServerGatewayImplTest {
    private static final int SERVER_PORT = 1080;
    private static final String USAGE_URL = "/sandboni/usage";

    @Test
    public void testSendCoverageData() throws Exception {
        ServerGatewayImpl serverGateway = mock(ServerGatewayImpl.class, Answers.CALLS_REAL_METHODS);
        doNothing().when(serverGateway).httpPost(any());

        Map<String, String> map = new HashMap<>();
        map.put("key1", "val1");
        map.put("key2", "val2");

        serverGateway.sendStats(map, "http://localhost:" + SERVER_PORT + "/" + USAGE_URL);
        assertTrue(true);
    }

    @Test(expected = GatewayException.class)
    public void testInvalidURL() throws Exception {
        ServerGatewayImpl serverGateway = mock(ServerGatewayImpl.class, Answers.CALLS_REAL_METHODS);
        doNothing().when(serverGateway).httpPost(any());

        Map<String, String> map = new HashMap<>();
        map.put("key1", "val1");
        map.put("key2", "val2");

        serverGateway.sendStats(map, null);
    }
}
