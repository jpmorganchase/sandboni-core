package com.sandboni.core.engine.scenario;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MustRunMethodTest {
    @Test
    public void testOne() {
        assertTrue(true);
    }

    @Test
    @IncludeTest
    public void testTwo() {
        assertTrue(true);
    }
}
