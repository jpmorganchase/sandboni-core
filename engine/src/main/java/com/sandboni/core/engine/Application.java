package com.sandboni.core.engine;

import com.sandboni.core.engine.result.ResultContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private Map<String, String> propertiesMap;

    private Arguments arguments;

    public static void main(String[] args) {
        Application app = new Application();
        app.buildArguments();
        Arguments arguments = app.getArguments();

        final Processor processor = new ProcessorBuilder()
                .with(pb -> pb.arguments = arguments)
                .build();
        processor
                .getResultGenerator().generate(ResultContent.ENTRY_POINTS);
    }

    //synchronized was added because of SonarQube demand
    Arguments buildArguments() {
        getProperties();

        arguments = new ArgumentsBuilder().with(ab->{
            ab.fromChangeId = getValue(SystemProperties.FROM);
            ab.toChangeId = getValue(SystemProperties.TO);
            ab.repository = getValue(SystemProperties.REPOSITORY);
            ab.srcLocations = new HashSet<>(Arrays.asList(getValue(SystemProperties.SRC_LOCATION).split(",")));
            ab.testLocations = new HashSet<>(Arrays.asList(getValue(SystemProperties.TEST_LOCATION).split(",")));
            ab.selectiveMode = Boolean.valueOf(getValue(SystemProperties.SELECTIVE_MODE));
            ab.gitCache = Boolean.valueOf(getValue(SystemProperties.GIT_CACHE));
            ab.coreCache = Boolean.valueOf(getValue(SystemProperties.CORE_CACHE));
        }).build();

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
        Map<String, String> pMap = System.getProperties().entrySet().stream()
                .filter(k-> ((String)k.getKey()).startsWith("sandboni."))
                .collect(Collectors.toMap(
                        e -> (String) e.getKey(),
                        e -> (String) e.getValue()));

        if (pMap.isEmpty()){
            printAvailableProperties();
            throw new IllegalArgumentException("No Sandboni properties were entered. please enter sufficient properties");
        }

        log.debug("Sandboni properties:{}", pMap);
        return pMap;
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
        log.debug("[property: {} required:{} ] retrieved value is {}", ppty.getName(), ppty.isRequired(), value  );
        if ((value == null || value.isEmpty())) {
            if (ppty.isRequired())
                throw new IllegalArgumentException(String.format("[%s] Property is missing or empty", ppty.getName()));

            value = (ppty.getDefaultValue() != null) ? ppty.getDefaultValue() : "";
        }
        return value.trim();
    }
}