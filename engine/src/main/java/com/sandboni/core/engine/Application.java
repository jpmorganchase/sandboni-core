package com.sandboni.core.engine;

import com.sandboni.core.engine.result.ResultContent;
import com.sandboni.core.engine.utils.StringUtil;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private Map<String, String> propertiesMap;

    private Arguments arguments;

    public static void main(String[] args) {

        Application app = new Application();
        app.buildArguments();
        Arguments arguments = app.getArguments();

        long start = System.nanoTime();
        final Processor processor = new ProcessorBuilder()
                .with(pb -> pb.arguments = arguments)
                .build();

        Set<TestVertex> relatedTests = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS).get(ResultContent.RELATED_TESTS);
        Set<TestVertex> disconnectedTests = processor.getResultGenerator().generate(ResultContent.DISCONNECTED_TESTS).get(ResultContent.DISCONNECTED_TESTS);
        Set<TestVertex> testSuites = processor.getResultGenerator().generate(ResultContent.TEST_SUITES).get(ResultContent.TEST_SUITES);

        log.info("Related tests to execute: {}", relatedTests);
        log.info("Related tests to execute (size): {}", relatedTests.size());
        log.info("Disconnected tests to execute: {}", disconnectedTests);
        log.info("Related test suites: {}", testSuites);

        log.info("Sandboni execution: {} milliseconds", TimeUtils.elapsedTime(start));
    }

    //synchronized was added because of SonarQube demand
    Arguments buildArguments() {
        getProperties();
        arguments = Arguments.builder()
                .fromChangeId(getValue(SystemProperties.FROM))
                .toChangeId(getValue(SystemProperties.TO))
                .repository(getValue(SystemProperties.REPOSITORY))
                .filter(getValue(SystemProperties.FILTER))
                .runSelectiveMode(Boolean.parseBoolean(getValue(SystemProperties.SELECTIVE_MODE)))
                .reportDir(getValue(SystemProperties.REPORT_DIR))
                .runAllExternalTests(Boolean.parseBoolean(getValue(SystemProperties.RUN_ALL_EXTERNAL_TESTS)))
                .gitCache(Boolean.parseBoolean(getValue(SystemProperties.GIT_CACHE)))
                .coreCache(Boolean.parseBoolean(getValue(SystemProperties.CORE_CACHE)))
                .srcLocation(getValue(SystemProperties.SRC_LOCATION).split(","))
                .testLocation(getValue(SystemProperties.TEST_LOCATION).split(","))
                .dependencies(getValue(SystemProperties.DEPENDENCIES).split(","))
                .outputFormat(getValue(SystemProperties.OUTPUT_FORMAT))
                .alwaysRunAnnotation(getValue(SystemProperties.ALWAYS_RUN_ANNOTATION))
                .enablePreview(Boolean.parseBoolean(getValue(SystemProperties.ENABLE_PREVIEW)))
                .useCliDiff(false)
                .ignoreUnsupportedFiles(Boolean.parseBoolean(getValue(SystemProperties.IGNORE_UNSUPPORTED_FILES)))
                .build();

        log.debug("[arguments collected] {}", arguments);
        return arguments;
    }

    public Arguments getArguments() {
        if (arguments == null) {
            arguments = buildArguments();
        }
        return arguments;
    }

    public Map<String, String> getProperties() {
        if (propertiesMap == null) {
            propertiesMap = preparePropertiesMap();
        }
        return propertiesMap;
    }

    private Map<String, String> preparePropertiesMap() {
        Map<String, String> properties = System.getProperties().entrySet().stream()
                .filter(k -> ((String) k.getKey()).startsWith("sandboni."))
                .collect(Collectors.toMap(
                        e -> (String) e.getKey(),
                        e -> (String) e.getValue()));

        if (properties.isEmpty()) {
            printAvailableProperties();
            throw new IllegalArgumentException("No Sandboni properties were entered. please enter sufficient properties");
        }

        log.debug("Sandboni properties:{}", properties);
        return properties;
    }

    private void printAvailableProperties() {
        StringBuilder builder = new StringBuilder("\nThere are the common Sandboni used:\n");
        Arrays.stream(SystemProperties.values()).forEach(s -> builder.append(String.format("%n\t %s \t%s\t[mandatory=%s, default value=%s]",
                s.getName(), s.getDescription(), s.isRequired(), s.getDefaultValue())));
        builder.append("\n");
        String output = builder.toString();
        log.info(output);

    }

    private String getValue(SystemProperties ppty) {
        String value = getProperties().get(ppty.getName());
        log.debug("[property: {} required:{} ] retrieved value is {}", ppty.getName(), ppty.isRequired(), value);
        if (StringUtil.isEmptyOrNull(value)) {
            if (ppty.isRequired())
                throw new IllegalArgumentException(String.format("[%s] Property is missing or empty", ppty.getName()));
            value = (ppty.getDefaultValue() != null) ? ppty.getDefaultValue() : "";
        }
        return value.trim();
    }
}