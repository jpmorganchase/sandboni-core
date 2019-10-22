package com.sandboni.core.engine;

public enum Stage {
    BUILD("BUILD_STAGE"),
    INTEGRATION("INTEGRATION_STAGE");

    private String name;

    public String getName() {
        return name;
    }

    Stage(String name) {
        this.name = name;
    }
}
