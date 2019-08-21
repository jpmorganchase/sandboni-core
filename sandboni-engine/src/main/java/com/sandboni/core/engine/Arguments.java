package com.sandboni.core.engine;

import java.util.*;

public class Arguments {

    public static final String INTEGRATION_STAGE = "INTEGRATION";
    public static final String BUILD_STAGE = "BUILD";

    private final String[] srcLocation;
    private final String[] testLocation;
    private final String filter;
    private final String fromChangeId;
    private final String toChangeId;
    private final String repository;
    private final String outputFormat;
    private final String trackingSort;
    private final boolean runSelectiveModeIfBuildFileHasChanged;
    private final boolean runAllExternalTests;
    private final boolean gitCache;
    private final boolean coreCache;
    private final String stage;
    private final String reportDir;

    @SuppressWarnings("squid:S00107")
    Arguments(String[] srcLocation,
              String[] testLocation,
              String filter,
              String fromChangeId,
              String toChangeId,
              String repository,
              String outputFormat,
              String trackingSort,
              String stage,
              String reportDir,
              boolean runSelectiveModeIfBuildFileHasChanged,
              boolean runAllExternalTests,
              boolean gitCache,
              boolean coreCache) {
        this.srcLocation = srcLocation;
        this.testLocation = testLocation;
        this.filter = filter;
        this.fromChangeId = fromChangeId;
        this.toChangeId = toChangeId;
        this.repository = repository;
        this.outputFormat = outputFormat;
        this.trackingSort = trackingSort;
        this.stage = stage;
        this.reportDir = reportDir;
        this.runSelectiveModeIfBuildFileHasChanged = runSelectiveModeIfBuildFileHasChanged;
        this.runAllExternalTests = runAllExternalTests;
        this.gitCache = gitCache;
        this.coreCache = coreCache;
    }

    public String[] getSrcLocation() {
        return srcLocation;
    }

    public String[] getTestLocation() {
        return testLocation;
    }

    public String getRepository() {
        return repository;
    }

    boolean isRunSelectiveModeIfBuildFileHasChanged() {
        return runSelectiveModeIfBuildFileHasChanged;
    }

    boolean isRunAllExternalTests() {
        return runAllExternalTests;
    }

    boolean isGitCache() {
        return gitCache;
    }

    boolean isCoreCache() {
        return coreCache;
    }

    public String getFilter() {
        return filter;
    }

    String getFromChangeId() {
        return fromChangeId;
    }

    String getToChangeId() {
        return toChangeId;
    }

    String getTrackingSort() {
        return trackingSort;
    }

    public String getOutputFormat() { return this.outputFormat;}

    public String getStage (){
        return stage;
    }

    public String getReportDir() {
        return reportDir;
    }

    public String toString() {
        return "source locations: " + Arrays.toString(srcLocation) +
                " | test locations: " + Arrays.toString(testLocation) +
                " | filter: " + filter +
                " | fromChangeId: " + fromChangeId +
                " | toChangeId: " + toChangeId +
                " | repository: " + repository +
                " | outputFormat: " + outputFormat +
                " | tracking.sort: " + trackingSort +
                " | stage: " + stage +
                " | reportDir: " + reportDir +
                " | runSelectiveModeIfBuildFileHasChanged: " + runSelectiveModeIfBuildFileHasChanged +
                " | runAllExternalTests: " + runAllExternalTests +
                " | gitCache: " + gitCache +
                " | coreCache: " + coreCache;
    }
}