package com.sandboni.core.engine.scenario.tests.client;

import com.sandboni.core.engine.scenario.lambda.client.RunnableClient;
import org.junit.Assert;
import org.junit.Test;

public class RunnableClientTest {

    @Test
    public void testRun() {
        Runnable runnable = new RunnableClient();
        runnable.run();
        Assert.assertTrue(true);
    }
}
