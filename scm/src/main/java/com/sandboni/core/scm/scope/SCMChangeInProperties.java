package com.sandboni.core.scm.scope;

import java.util.Set;

public class SCMChangeInProperties extends SCMChange {

    protected Set<String> keys;

    public SCMChangeInProperties(String fileName, Set<Integer> linesChanged, Set<String> keys, ChangeType type) {
        super(fileName, linesChanged, type);
        this.keys = keys;
    }
}
