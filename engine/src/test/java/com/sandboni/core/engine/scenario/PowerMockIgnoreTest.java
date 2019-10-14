package com.sandboni.core.engine.scenario;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import static org.junit.Assert.assertTrue;

@PowerMockIgnore({ "javax.management.*" })
public class PowerMockIgnoreTest {
    @Test
    public void testSomeMethod() {
        assertTrue(true);
    }
}
