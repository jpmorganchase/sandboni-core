package com.sandboni.core.engine.scenario;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AlwaysRunMethodTest {
    @Test
    public void testOne() {
        assertTrue(true);
    }

    @Test
    @AlwaysRun
    public void testTwo() {
        assertTrue(true);
    }
}
