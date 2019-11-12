package com.sandboni.core.engine.scenario;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestSuiteRunner {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestSuite1.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(String.format("Failure found: %s", failure.toString()));
        }
        System.out.println(String.format("Result: %s", (result.wasSuccessful()? "Success": "Failure")));
    }
}
