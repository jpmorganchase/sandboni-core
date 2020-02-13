package com.sandboni.core.engine.filter;

public interface ScopeFilter<T, U> {

    boolean isInScope(T changeScope, U mainSourceDirs);
}
