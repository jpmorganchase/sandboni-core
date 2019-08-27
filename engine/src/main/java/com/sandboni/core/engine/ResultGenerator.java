package com.sandboni.core.engine;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.FileOptions;
import com.sandboni.core.engine.render.file.FileType;
import com.sandboni.core.engine.render.file.FileWriterEngine;
import com.sandboni.core.engine.result.Result;
import com.sandboni.core.engine.result.ResultContent;
import com.sandboni.core.engine.result.Status;
import com.sandboni.core.engine.sta.Builder;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ResultGenerator {

    private static final Logger log = LoggerFactory.getLogger(ResultGenerator.class);

    public static final String TESTS_OUTPUT = "%s/sandboni-tests.%s";
    public static final String JIRA_TRACKING_FILE_NAME = "sandboni-jira-connect.csv";

    private Result result;
    private final Supplier<Builder> builderSupplier;
    private final Arguments arguments;
    private FileOptions fileOptions;

    ResultGenerator(Supplier<Builder> builderSupplier, Arguments arguments) {
        this.builderSupplier = builderSupplier;
        this.arguments = arguments;
        this.result = new Result();
    }

    public Result generate(ResultContent... resultContents) {
        log.debug("....generate result for {}", Arrays.stream(resultContents).collect(Collectors.toList()));

        Arrays.stream(resultContents)
                .parallel()
                .forEach(rc -> {
                    Object data = getContent(rc);

                    Class clazz = Objects.requireNonNull(data).getClass();
                    result.put(rc, clazz, data);

                    if (rc.isOutputToFile()) { //if we want to output to file
                        try {
                            FileWriterEngine.write(result, fileOptions);
                        } catch (RendererException e) {
                            log.warn("Unable to render file: {}, error: {}", fileOptions.getName(), e);
                        }
                    }
                });

        if (!result.isError()) {
            result.setStatus(Status.OK);
        }
        result.setFilterIndicator(builderSupplier.get().getFilterIndicator());
        return result;
    }

    private Object getContent(ResultContent rc) {
        switch (rc) {
            case ALL_TESTS:
                return builderSupplier.get().getAllEntryPoints().collect(Collectors.toSet());
            case RELATED_TESTS:
                return builderSupplier.get().getEntryPoints().collect(Collectors.toSet());
            case DISCONNECTED_TESTS:
                return builderSupplier.get().getDisconnectedEntryPoints().collect(Collectors.toSet());
            case ALL_EXTERNAL_TESTS:
                return builderSupplier.get().getAllEntryPoints()
                        .collect(Collectors.groupingBy(Vertex::getActor, Collectors.mapping(Vertex::getAction, Collectors.toSet())));
            case EXIT_POINTS:
                return builderSupplier.get().getExitPoints()
                        .collect(Collectors.groupingBy(Vertex::getActor,
                                Collectors.mapping(Vertex::getAction, Collectors.toSet())));
            case ENTRY_POINTS:
                return builderSupplier.get().getEntryPoints()
                        .collect(Collectors.groupingBy(Vertex::getActor, Collectors.mapping(Vertex::getAction, Collectors.toSet())));
            case DISCONNECTED_ENTRY_POINTS:
                return builderSupplier.get().getDisconnectedEntryPoints()
                .collect(Collectors.groupingBy(Vertex::getActor, Collectors.mapping(Vertex::getAction, Collectors.toSet())));
            case UNREACHABLE_EXIT_POINTS:
                return builderSupplier.get().getUnreachableExitPoints()
                .collect(Collectors.groupingBy(Vertex::getActor, Collectors.mapping(Vertex::getAction, Collectors.toSet())));
            case JIRA_TRACKING:
                prepareFileOptions(FileType.CSV, JIRA_TRACKING_FILE_NAME);
                return builderSupplier.get().getJiraList().collect(Collectors.toSet());
            case RELATED_TEST_TO_FILE:
                prepareFileOptions(arguments.getOutputFormat(), TESTS_OUTPUT);
                return builderSupplier.get().getEntryPoints().collect(Collectors.toSet());
            default:
                 return null;
        }
    }

    private void prepareFileOptions(String outputFormat, String fileName) {
        prepareFileOptions(FileType.valueOf(outputFormat.toUpperCase()),
                String.format(fileName, arguments.getReportDir(), outputFormat.toLowerCase()));
    }

    private void prepareFileOptions(FileType type, String fileName){
        log.info("Preparing file options for '{}'", fileName);
        fileOptions = new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = fileName;
                    fo.type = type ;})
                .build();
    }
}
