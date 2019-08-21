package com.sandboni.core.engine.scenario;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class EmptyTest {

    @Test
    @Ignore
    @SuppressWarnings("squid:S1607")
    public void testIgnoredMethod(){
        Assert.assertTrue(true);
    }

    @Test
    public void testNotIgnoredMethod(){
        Assert.assertTrue(true);
    }
}
