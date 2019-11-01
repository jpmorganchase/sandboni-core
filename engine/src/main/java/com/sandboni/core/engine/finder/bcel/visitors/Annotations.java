package com.sandboni.core.engine.finder.bcel.visitors;

public enum Annotations {

    ;public enum TEST{
        IGNORE("org/junit/Ignore"),
        BEFORE("Before"),
        AFTER("After"),
        TEST("Test"),
        RUN_WITH("RunWith"),
        RUN_WITH_VALUE_SUITE("org/junit/runners/Suite"),
        SUITE_CLASSES("Suite$SuiteClasses");

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
