package com.sandboni.core.scm.scope.analysis;

import com.sandboni.core.scm.scope.Change;

@FunctionalInterface
public interface ChangeScanner {

    boolean scan(Change change);

}
