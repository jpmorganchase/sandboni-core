package com.sandboni.core.engine.config;

public enum ConfigProp {
    SERVER_BASE_URL("serverBaseUrl"),
    SAVE_COVERAGE("saveCoverage");

    private String key;

    ConfigProp(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}