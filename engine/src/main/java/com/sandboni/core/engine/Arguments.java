package com.sandboni.core.engine;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class Arguments {
    private String[] srcLocation;
    private String[] testLocation;
    private String[] dependencies;
    private String applicationId;
    private String filter;
    private String fromChangeId;
    private String toChangeId;
    private String repository;
    private String outputFormat;
    private String trackingSort;
    private boolean runSelectiveMode;
    private boolean runAllExternalTests;
    private boolean gitCache;
    private boolean coreCache;
    private String stage;
    private String reportDir;
    private String alwaysRunAnnotation;
    private String seloniFilePath;
    private boolean enableExperimental;
}