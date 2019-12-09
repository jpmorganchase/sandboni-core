package com.sandboni.core.engine.contract;

import java.util.Collections;
import java.util.List;

public class JsonEntry {
    private List<String> urls;
    private String className;
    private String testName;
    private String date;
    private String type;
    private String status;
    private String lineNumber;
    private String filepath;

    public List<String> getUrls() {
        return Collections.unmodifiableList(urls);
    }

    public String getClassName() {
        return className;
    }

    public String getTestName() {
        return testName;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public String getFilepath() {
        return filepath;
    }


    public String getDate() {
        return date;
    }
}
