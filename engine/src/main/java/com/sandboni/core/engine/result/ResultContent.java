package com.sandboni.core.engine.result;

public enum ResultContent {

    ALL_TESTS,
    ALL_EXTERNAL_TESTS,
    RELATED_TESTS,
    TEST_SUITES,
    DISCONNECTED_TESTS,
    CHANGES,
    UNREACHABLE_CHANGES,
    JIRA_TRACKING(true),
    RELATED_TEST_TO_FILE(true),
    OUTPUT_TO_FILE(true),
    CHANGE_STATS,
    FORMATTED_CHANGE_STATS(true),
    RELATED_UNIT,
    RELATED_CUCUMBER,
    DISCONNECTED_UNIT,
    DISCONNECTED_CUCUMBER,
    RELATED_EXTERNAL_UNIT,
    RELATED_EXTERNAL_CUCUMBER,
    ALL_EXTERNAL_UNIT,
    ALL_EXTERNAL_CUCUMBER,
    INCLUDED_BY_ANNOTATION,
    CUCUMBER_RUNNERS;

    private boolean outputToFile;

    ResultContent() {
    }

    ResultContent(boolean outputToFile) {
        this.outputToFile = outputToFile;
    }

    public boolean isOutputToFile() {
        return outputToFile;
    }
}