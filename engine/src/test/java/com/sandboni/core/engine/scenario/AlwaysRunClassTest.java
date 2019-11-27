package com.sandboni.core.engine.scenario;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

@AlwaysRun
public class AlwaysRunClassTest {
    @Test
    public void testOne() {
        assertTrue(true);
    }

    @Test
    public void testTwo() {
        assertTrue(true);
    }
}
