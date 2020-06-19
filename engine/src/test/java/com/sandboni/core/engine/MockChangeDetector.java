package com.sandboni.core.engine;

import com.sandboni.core.engine.contract.ChangeDetector;
import com.sandboni.core.scm.scope.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockChangeDetector implements ChangeDetector {

    public static final String PACKAGE_NAME = "com.sandboni.core.engine.scenario";
    public static final String JUNIT5 = "junit5";
    public static final String LAMBDA_PACKAGE_NAME = "com.sandboni.core.engine.scenario.lambda";
    public static final String LAMBDA_PACKAGE_TEST_NAME = "com.sandboni.core.engine.scenario.tests";
    public static final String TEST_LOCATION = "./target/test-classes";

    @Override
    public ChangeScope<Change> getChanges(String fromChangeId, String toChangeId) {
        ChangeScope<Change> changeScope = new ChangeScopeImpl();
        // complete file is new
        changeScope.addChange(new SCMChange(PACKAGE_NAME.replace('.', '/') + "/Callee.java",
                IntStream.range(1, 100).boxed().collect(Collectors.toSet()), ChangeType.ADD ));

        // one method
        changeScope.addChange(new SCMChange(PACKAGE_NAME.replace('.', '/') + "/CallerBase.java",
                IntStream.range(8, 9).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        return  changeScope;
    }
}
