package com.sandboni.core.engine.common;

public enum ExtensionType {

    JAR(".jar"), CLASS(".class"), FEATURE(".feature"), SANDBONI(".sandboni");

    private String type;

    ExtensionType(String type){
        this.type = type;
    }

    public String type() {
        return type;
    }
}
