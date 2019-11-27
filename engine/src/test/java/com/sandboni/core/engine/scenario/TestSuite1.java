package com.sandboni.core.engine.scenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.assertTrue;

@RunWith(Suite.class)
@Suite.SuiteClasses({SuiteTestClass1.class, SuiteTestClass2.class})
public class TestSuite1 {

    @Test
    // won't run, and should not be included in any result set
    public void testPrint(){
        System.out.println(this.getClass().getSimpleName());
        assertTrue(true);
    }

}
