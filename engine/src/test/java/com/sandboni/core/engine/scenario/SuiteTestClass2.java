package com.sandboni.core.engine.scenario;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SuiteTestClass2 {
    private final Callee c;

    public SuiteTestClass2() {
        this.c = new Callee();
    }

    @Test
    public void testPrint(){
        System.out.println(this.getClass().getSimpleName());
        c.doStuffDefault();
        assertTrue(true);
    }

}
