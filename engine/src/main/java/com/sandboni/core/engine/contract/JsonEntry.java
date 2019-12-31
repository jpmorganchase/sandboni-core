package com.sandboni.core.engine.contract;

import lombok.Getter;

import java.util.List;

@Getter
public class JsonEntry {
    private List<String> urls;
    private String className;
    private String testName;
    private String date;
    private String type;
    private String status;
    private String lineNumber;
    private String filepath;
}
