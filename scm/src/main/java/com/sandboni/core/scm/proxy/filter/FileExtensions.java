package com.sandboni.core.scm.proxy.filter;

import java.util.Arrays;

public enum FileExtensions {
    JAVA(".java"), FEATURE(".feature"), XML(".xml"), YML(".yml"), PROPERTIES(".properties"), GRADLE(".gradle"), PROPS(".props");

    public String extension() {
        return ext;
    }

    private String ext;

    FileExtensions(String ext) {
        this.ext = ext;
    }

    public boolean in(FileExtensions... extensions) {
        return Arrays.stream(extensions).anyMatch(e -> e == this);
    }

    public static FileExtensions fromText(String extension) {
        return Arrays.stream(values()).filter(e-> e.ext.equalsIgnoreCase(extension)).findFirst().orElse(null);
    }
}
