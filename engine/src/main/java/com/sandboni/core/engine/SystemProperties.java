package com.sandboni.core.engine;

public enum SystemProperties {

    SRC_LOCATION("sandboni.source.locations","List of source locations where Sandboni will be executed", true ),
    TEST_LOCATION("sandboni.test.locations","List of tests locations where Sandboni will be executed", true ),
    DEPENDENCIES("sandboni.dependencies","List of external dependencies that will be analyzed by Sandboni", false ),
    FROM("sandboni.scm.from", "The From commit id for determine the commits range", true),
    TO("sandboni.scm.to", "The To commit id for determine the commits range", true),
    REPOSITORY("sandboni.scm.repository", "Git repository location", true),
    OUTPUTS("sandboni.outputs", "List of preferab;e outputs", false, "tests"),
    OUTPUT_FORMAT("sandboni.output.format", "Format to output to", false, "csv"),
    STAGE("sandboni.stage", "Build Stage", false, Stage.BUILD.name()),
    REPORT_DIR("sandboni.reportDir", "Report folder", false),
    SELECTIVE_MODE("sandboni.selectiveMode",  "Run in selective mode",false, Boolean.FALSE.toString()),
    RUN_ALL_EXTERNAL_TESTS("sandboni.runAllExternalTests", "Run all external tests", false, Boolean.FALSE.toString()),
    GIT_CACHE("sandboni.gitCache", "Caches changes found in Git repository", false, Boolean.FALSE.toString()),
    CORE_CACHE("sandboni.coreCache", "Caches core internal data", false, Boolean.FALSE.toString()),
    FILTER("sandboni.filter", "Filter elements pattern", false),
    ALWAYS_RUN_ANNOTATION("sandboni.alwaysRunAnnotation", "Always run annotation", false),
    SELONI_FILEPATH("seloni.filepath", "Seloni json file path", false),
    ENABLE_PREVIEW("sandboni.enable.preview", "Enable preview of new features", false),
    IGNORE_UNSUPPORTED_FILES("sandboni.ignore.unsupported.files", "Ignore changes in unsupported files", false, Boolean.TRUE.toString());

    SystemProperties(String name, String description , boolean required) {
        this.name = name;
        this.required = required;
        this.description = description;
    }

    SystemProperties(String name, String description, boolean required, String defaultValue) {
        this.name = name;
        this.required = required;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    private boolean required;
    private String name;
    private String defaultValue;
    private String description;

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }
}
