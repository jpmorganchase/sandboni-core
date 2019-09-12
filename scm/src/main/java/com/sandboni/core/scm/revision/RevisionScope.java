package com.sandboni.core.scm.revision;

public interface RevisionScope<T> {
    T getFrom();
    T getTo();
}
