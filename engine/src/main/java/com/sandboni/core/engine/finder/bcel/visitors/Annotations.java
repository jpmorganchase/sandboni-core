package com.sandboni.core.engine.finder.bcel.visitors;

public enum Annotations {

    ;public enum TEST{
        IGNORE("org/junit/Ignore"),
        DISABLED("org/junit/jupiter/api/Disabled"), //Junit 5
        BEFORE("Before"),
        BEFORE_EACH("BeforeEach"), //Junit 5
        AFTER("After"),
        AFTER_EACH("AfterEach"), //Junit 5
        TEST("Test"),
        CUCUMBER_OPTIONS("cucumber/api/CucumberOptions"),
        RUN_WITH("org/junit/runner/RunWith"),
        EXTEND_WITH("org/junit/jupiter/api/extension/ExtendWith"), //Junit 5
        RUN_WITH_VALUE_SUITE("org/junit/runners/Suite"),
        SUITE_CLASSES("Suite$SuiteClasses"),
        CATEGORY("categories/Category"),
        TAG("Tag"); //Junit 5

        private String desc;

        TEST(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum SPRING{
        CONTROLLER("Controller"),
        REQUEST_MAPPING("RequestMapping"),
        GET_MAPPING("GetMapping"),
        DELETE_MAPPING("DeleteMapping"),
        POST_MAPPING("PostMapping"),
        PUT_MAPPING("PutMapping"),
        PATCH_MAPPING("PatchMapping");

        private String desc;

        SPRING(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum JAVAX{
        PATH("Path"),
        OPTIONS("Options");

        private String desc;

        JAVAX(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}
