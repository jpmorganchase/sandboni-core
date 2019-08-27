package com.sandboni.core.engine.contract;

import java.util.Collections;
import java.util.List;

public class JsonEntry {
    private List<String> urls;
    private String className;
    private String testName;
    private String date;

    public List<String> getUrls() {
        return Collections.unmodifiableList(urls);
    }

    public String getClassName() {
        return className;
    }

    public String getTestName() {
        return testName;
    }

    public String getDate() {
        return date;
    }
}
