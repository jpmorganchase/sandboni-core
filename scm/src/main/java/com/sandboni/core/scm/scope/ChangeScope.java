package com.sandboni.core.scm.scope;

import com.sandboni.core.scm.proxy.filter.FileExtensions;

import java.util.List;
import java.util.Set;

public interface ChangeScope<T> {

    void addChange(T change);

    List<Change> getChanges(String className);

    List<Change> getChangesForFilesEndWith(String fileName);

    boolean contains(String className);

    Set<Integer> getAllLinesChanged(String className);

    Set<String> getAllAffectedClasses();

    void remove(String className);

    void remove(FileExtensions... fe);

    void include(FileExtensions... fe);

    boolean isEmpty();

}
