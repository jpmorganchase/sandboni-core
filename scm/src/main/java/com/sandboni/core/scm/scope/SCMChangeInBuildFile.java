package com.sandboni.core.scm.scope;

import org.apache.maven.model.Model;

import java.util.Optional;
import java.util.Set;

public class SCMChangeInBuildFile extends SCMChange {

    private final String fileContent;

    private final Model model;

    SCMChangeInBuildFile(String className, Set<Integer> linesChanged, ChangeType type,
                         String fileContent, Model model) {
        super(className, linesChanged, type);
        this.fileContent = fileContent;
        this.model = model;
    }

    @Override
    public Optional<String> getFileContent() {
        return Optional.ofNullable(fileContent);
    }

    @Override
    public Optional<Model> getModel() {
        return Optional.ofNullable(model);
    }
}