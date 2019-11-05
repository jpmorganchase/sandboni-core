package com.sandboni.core.engine.scenario;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestSuiteExampleRunner {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestSuiteExample.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(String.format("Failure found: %s", failure.toString()));
        }
        System.out.println(String.format("Result: %s", (result.wasSuccessful()? "Success": "Failure")));
    }
}
