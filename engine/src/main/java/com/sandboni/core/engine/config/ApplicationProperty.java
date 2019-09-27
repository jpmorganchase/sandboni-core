package com.sandboni.core.engine.config;

public enum ApplicationProperty {
    SERVER_BASE_URL("serverBaseUrl"),
    SAVE_COVERAGE("saveCoverage");

    private String key;

    ApplicationProperty(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}