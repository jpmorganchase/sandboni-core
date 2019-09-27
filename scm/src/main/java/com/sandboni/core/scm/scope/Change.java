package com.sandboni.core.scm.scope;

import org.apache.maven.model.Model;

import java.util.Optional;
import java.util.Set;

public interface Change {

    String getFilename();

    Set<Integer> getLinesChanged();

    ChangeType getType();

    void addChangedLines(Set<Integer> set);

    Optional<String> getFileContent();

    Optional<Model> getModel();

}
