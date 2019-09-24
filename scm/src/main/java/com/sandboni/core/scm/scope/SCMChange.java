package com.sandboni.core.scm.scope;

import org.apache.maven.model.Model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SCMChange implements Change {

    private String fileName;
    private Set<Integer> linesChanged;
    protected ChangeType type;

    public SCMChange(String className, Set<Integer> linesChanged, ChangeType type) {
        this.fileName = className;
        this.linesChanged = new HashSet<>(linesChanged);
        this.type = type;
    }

    @Override
    public String getFilename() {return fileName;}

    @Override
    public Set<Integer> getLinesChanged() {
        return Collections.unmodifiableSet(linesChanged);
    }

    @Override
    public ChangeType getType() {
        return type;
    }

    @Override
    public void addChangedLines(Set<Integer> set) {
        this.linesChanged.addAll(set);
    }

    @Override
    public Optional<String> getFileContent() {
        return Optional.empty();
    }

    @Override
    public Optional<Model> getModel() {
        return Optional.empty();
    }
}
