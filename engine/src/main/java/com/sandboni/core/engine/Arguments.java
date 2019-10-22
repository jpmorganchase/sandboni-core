package com.sandboni.core.engine;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Builder
@ToString
@Getter
public class Arguments {
    private Set<String> srcLocation;
    private Set<String> testLocation;
    private Set<String> dependencies;
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
    private Set<String> includeAnnotations;
}