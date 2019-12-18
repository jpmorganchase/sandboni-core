package com.sandboni.core.engine.sta.graph;

public enum LinkType {
    // for invokevirtual calls
    METHOD_CALL("Method call", true),

    // for invokeinterface calls
    INTERFACE_CALL("Interface call", true),

    // for invokespecial calls
    SPECIAL_CALL("Special call", true),

    // for invokestatic calls
    STATIC_CALL("Static call", true),

    // for invokedynamic calls
    DYNAMIC_CALL("Dynamic call", true),

    // for field references
    FIELD_GET("Read field", true),

    // for field references
    FIELD_PUT("Write field", true),

    // for static field references
    STATIC_GET("Read static field", true),

    // for static field references
    STATIC_PUT("Write static field", true),

    // for interface implementations
    INTERFACE_IMPL("Interface implementation", true),

    //forwards implementation to parent
    FORWARD_TO("Inherited implementation", true),

    //override implementation to parent
    OVERRIDDEN("Overridden implementation", true),

    IMPLICIT("Implicit call", false),

    // entry
    ENTRY_POINT("Entry point", false),

    // exit
    EXIT_POINT("Change", false),

    // link http
    HTTP_REQUEST("HTTP request", false),

    // invoke by url
    HTTP_HANLDER("HTTP Handler", false),

    // invoke my pattern matching
    HTTP_MAP("HTTP Pattern", false),

    HTTP_MAP_SELONI("HTTP Pattern Seloni", false),

    // manual mapping
    MANUAL("Manually mapped", false),

    // location link
    CONVENTION("Convention", false),

    // metadata
    METADATA("Metadata", false),

    CUCUMBER_SOURCE("Cucumber Source Code", true),

    CUCUMBER_TEST("Cucumber Test Method", true),

    CUCUMBER_TEST_TAG("Cucumber Test Method Tag", true),

    CUCUMBER_MAP("Cucumber Map", false),

    CUCUMBER_RUNNER("Cucumber Runner", true),

    TEST_SUITE("Test Suite", false);

    private String description;

    private Boolean isCodeBased;

    LinkType(String description, boolean isCodeBased) {
        this.description = description;
        this.isCodeBased = isCodeBased;
    }

    public String description() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    public Boolean isCodeBased() {
        return isCodeBased;
    }
}
