package com.sandboni.core.engine.scenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SuiteTestClass1.class, SuiteTestClass2.class, SuiteTestClass3.class})
public class TestSuiteExample {

    @Test
    // won't run
    public void print(){
        System.out.println("TestSuiteExample");
    }

}
