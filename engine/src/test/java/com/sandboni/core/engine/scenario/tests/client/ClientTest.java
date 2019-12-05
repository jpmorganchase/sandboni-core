package com.sandboni.core.engine.scenario.tests.client;

import com.sandboni.core.engine.scenario.lambda.Client;
import org.junit.Assert;
import org.junit.Test;

public class ClientTest {

    @Test
    public void testClient() {
        Client client = new Client();
        Assert.assertEquals(client.getValue(), (Long) 100L);
    }
}
