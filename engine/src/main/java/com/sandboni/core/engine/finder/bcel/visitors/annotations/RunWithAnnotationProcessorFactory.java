package com.sandboni.core.engine.finder.bcel.visitors.annotations;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.sandboni.core.engine.finder.bcel.visitors.Annotations.TEST.RUN_WITH_VALUE_SUITE;

public class RunWithAnnotationProcessorFactory {

    private static final String CUCUMBER_RUNNER_CLASS = "cucumber/api/junit/Cucumber";

    private static final Map<String, Supplier<RunWithAnnotationProcessor>> map = new HashMap<>();

    private static final String DEFAULT_IMPL = "Default";

    static {
        map.put(CUCUMBER_RUNNER_CLASS, CucumberAnnotationProcessor::new);
        //todo add support for cucumber with JUnit 5
        map.put(RUN_WITH_VALUE_SUITE.getDesc(), SuiteAnnotationProcessor::new);
        //todo add support for test suite with JUnit 5
        map.put(DEFAULT_IMPL, RunWithAnnotationProcessorDefaultImpl::new);
    }


    public RunWithAnnotationProcessor getProcessor(String runWithAnnotationValue) {
        Supplier<RunWithAnnotationProcessor> processorSupplier = map.get(runWithAnnotationValue);
        if(processorSupplier != null) {
            return processorSupplier.get();
        } else {
            return map.get(DEFAULT_IMPL).get();
        }

    }


}


