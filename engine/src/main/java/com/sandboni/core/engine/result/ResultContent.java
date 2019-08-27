package com.sandboni.core.engine.result;

public enum ResultContent {

    ALL_TESTS,
    RELATED_TESTS,
    DISCONNECTED_TESTS,
    EXIT_POINTS,
    ENTRY_POINTS,
    DISCONNECTED_ENTRY_POINTS,
    UNREACHABLE_EXIT_POINTS,
    ALL_EXTERNAL_TESTS,
    JIRA_TRACKING(true),
    RELATED_TEST_TO_FILE(true),
    OUTPUT_TO_FILE(true);

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