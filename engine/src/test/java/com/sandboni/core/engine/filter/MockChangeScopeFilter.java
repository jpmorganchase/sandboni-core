package com.sandboni.core.engine.filter;

import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;

import java.io.File;
import java.util.Set;

public class MockChangeScopeFilter implements ScopeFilter<ChangeScope<Change>, Set<File>> {

    @Override
    public boolean isInScope(ChangeScope<Change> changeScope, Set<File> mainSourceDirs) {
        return true;
    }
}
