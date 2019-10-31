package com.sandboni.core.engine;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.FileOptions;
import com.sandboni.core.engine.render.file.FileType;
import com.sandboni.core.engine.render.file.FileWriterEngine;
import com.sandboni.core.engine.result.FilterIndicator;
import com.sandboni.core.engine.result.Result;
import com.sandboni.core.engine.result.ResultContent;
import com.sandboni.core.engine.result.Status;
import com.sandboni.core.engine.sta.operation.GraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResultGenerator {

    private static final Logger log = LoggerFactory.getLogger(ResultGenerator.class);

    public static final String TESTS_OUTPUT = "%s/sandboni-tests.%s";
    public static final String CHANGE_STATS_OUTPUT = "%s/sandboni-change-stats.%s";
    public static final String JIRA_TRACKING_FILE_NAME = "sandboni-jira-connect.csv";

    private final Result result;
    private final Arguments arguments;
    private final GraphOperations graphOperations;
    private final FilterIndicator filterIndicator;
    private final String skipReason;

    ResultGenerator(GraphOperations graphOperations, Arguments arguments, FilterIndicator filterIndicator, String skipReason) {
        this.graphOperations = graphOperations;
        this.arguments = arguments;
        this.filterIndicator = filterIndicator;
        this.skipReason = skipReason;
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
                        writeOutputToFile(result, rc);
                    }
                });

        if (!result.isError()) {
            result.setStatus(Status.OK);
        }
        result.setFilterIndicator(filterIndicator);
        return result;
    }

    public FilterIndicator getFilterIndicator() {
        return filterIndicator;
    }

    private void writeOutputToFile(Result result, ResultContent resultContent) {
        try {
            FileWriterEngine.write(result.get(resultContent), getFileOptions(resultContent));
        } catch (RendererException e) {
            log.warn("Unable to render file", e);
        }
    }

    private FileOptions getFileOptions(ResultContent rc) throws RendererException {
        switch (rc) {
            case JIRA_TRACKING:
                return prepareFileOptions(FileType.CSV, JIRA_TRACKING_FILE_NAME);
            case RELATED_TEST_TO_FILE:
                return prepareFileOptions(arguments.getOutputFormat(), TESTS_OUTPUT);
            case FORMATTED_CHANGE_STATS:
                return prepareFileOptions(FileType.JSON, CHANGE_STATS_OUTPUT);
            default:
                throw new RendererException("Invalid ResultContent to write to file");
        }
    }

    private FileOptions prepareFileOptions(String outputFormat, String fileName) {
        return prepareFileOptions(FileType.valueOf(outputFormat.toUpperCase()), fileName);
    }

    private FileOptions prepareFileOptions(FileType type, String fileName){
        File reportDir = new File(arguments.getReportDir());
        reportDir.mkdirs();

        String filePath = String.format(fileName, arguments.getReportDir(), type.name().toLowerCase());
        log.debug("Preparing file options for '{}'", filePath);
        return new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = filePath;
                    fo.type = type ;})
                .build();
    }

    private Object getContent(ResultContent rc) {
        switch (rc) {
            case ALL_TESTS:
                return graphOperations.getAllTests();
            case DISCONNECTED_TESTS:
                return graphOperations.getDisconnectedTests();
            case CHANGES:
                return graphOperations.getChangesMap();
            case RELATED_TESTS:
            case RELATED_TEST_TO_FILE:
                return graphOperations.getRelatedTests();
            case UNREACHABLE_CHANGES:
                return graphOperations.getUnreachableChanges();
            case JIRA_TRACKING:
                return graphOperations.getJiraTracking();
            case CHANGE_STATS:
                return graphOperations.getChangeStats();
            case FORMATTED_CHANGE_STATS:
                return graphOperations.getFormattedChangeStats();
            case RELATED_UNIT:
                return graphOperations.getUnitRelatedTests();
            case RELATED_CUCUMBER:
                return graphOperations.getCucumberRelatedTests();
            case DISCONNECTED_UNIT:
                return graphOperations.getUnitDisconnectedTests();
            case DISCONNECTED_CUCUMBER:
                return graphOperations.getCucumberDisconnectedTests();
            case RELATED_EXTERNAL_UNIT:
                return graphOperations.getUnitRelatedExternalTests();
            case RELATED_EXTERNAL_CUCUMBER:
                return graphOperations.getCucumberRelatedExternalTests();
            case ALL_EXTERNAL_UNIT:
                return graphOperations.getAllExternalUnitTests();
            case ALL_EXTERNAL_CUCUMBER:
                return graphOperations.getAllExternalCucumberTests();
            case INCLUDED_BY_ANNOTATION:
                return graphOperations.getIncludedByAnnotationTest();
            default:
                 return null;
        }
    }

    public String getSkipReason() {
        return skipReason;
    }
}
