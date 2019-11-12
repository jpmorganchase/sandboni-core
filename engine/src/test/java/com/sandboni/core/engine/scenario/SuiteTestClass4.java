package com.sandboni.core.engine.scenario;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SuiteTestClass4 {

    @Test
    public void testPrint(){
        System.out.println(this.getClass().getSimpleName());
        Callee.doStuffStatic();
        assertTrue(true);
    }

}
