package com.sandboni.core.engine.contract;

import com.sandboni.core.scm.scope.ChangeScope;

import java.io.IOException;

@FunctionalInterface
public interface ChangeDetector<T>{

    ChangeScope<T> getChanges(String fromRev, String toRev) throws IOException;
}