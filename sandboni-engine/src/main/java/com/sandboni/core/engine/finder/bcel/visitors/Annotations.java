package com.sandboni.core.engine.finder.bcel.visitors;

public enum Annotations {

    ;public enum TEST{
        IGNORE("Ignore"),
        BEFORE("Before"),
        AFTER("After");

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
