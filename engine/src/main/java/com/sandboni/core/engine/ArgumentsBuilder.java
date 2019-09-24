package com.sandboni.core.engine;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ArgumentsBuilder implements BuilderPattern<Arguments, ArgumentsBuilder> {

    public String fromChangeId;
    public String toChangeId;
    public String repository;
    public String filter;
    public String trackingSort;
    public boolean selectiveMode;
    public String stage;
    public String reportDir = ".";
    public boolean runAllExternalTests;
    public boolean gitCache;
    public boolean coreCache;
    public String applicationId;
    public Set<String> srcLocations;
    public Set<String> testLocations;
    public Set<String> dependencies;
    public String outputFormat = "csv";

    public ArgumentsBuilder(){
        this.srcLocations = new HashSet<>();
        this.testLocations = new HashSet<>();
        this.dependencies = new HashSet<>();
    }

    @Override
    public ArgumentsBuilder with(Consumer<ArgumentsBuilder> builderFunction) {
        builderFunction.accept(this);
        return this;
    }

    public Arguments build() {
        return new Arguments(
                srcLocations.toArray(new String[0]),
                testLocations.toArray(new String[0]),
                dependencies.toArray(new String[0]),
                filter,
                fromChangeId,
                toChangeId,
                repository,
                outputFormat,
                trackingSort,
                stage,
                reportDir,
                selectiveMode,
                runAllExternalTests,
                gitCache,
                coreCache,
                applicationId);
    }
}
