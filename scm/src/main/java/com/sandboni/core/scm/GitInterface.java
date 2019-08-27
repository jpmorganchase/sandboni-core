package com.sandboni.core.scm;

import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;

import java.util.List;
import java.util.Set;

public interface GitInterface {
    ChangeScope<Change> getChanges(String fromRev, String toRev) throws SourceControlException;

    Set<RevInfo> getJiraSet(String packagePath, List<Integer> lineNumbers) throws SourceControlException;
}
