package com.sandboni.core.scm.scope;

import java.util.Set;

public interface Change {

    String getFilename();

    Set<Integer> getLinesChanged();

    ChangeType getType();

    void addChangedLines(Set<Integer> set);

}
