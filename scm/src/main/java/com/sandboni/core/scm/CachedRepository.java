package com.sandboni.core.scm;

import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.exception.SourceControlRuntimeException;
import com.sandboni.core.scm.proxy.SourceControlFilter;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedRepository extends GitRepository {

    // Is safe to have these objects living in parallel execution because change scope doesn't change
    // between multiple projects during build lifecycle
    private static ConcurrentMap<String, ChangeScope<Change>> changesCache = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Set<RevInfo>> blameCache = new ConcurrentHashMap<>(1000);

    public CachedRepository(String repositoryPath, SourceControlFilter... filters) {
        super(repositoryPath, filters);
    }

    @Override
    public ChangeScope<Change> getChanges(String fromRev, String toRev) {
        final String key = fromRev + toRev;
        return changesCache.computeIfAbsent(key, k -> {
            try {
                return super.getChanges(fromRev, toRev);
            } catch (SourceControlException e) {
                throw new SourceControlRuntimeException(e);
            }
        });
    }

    @Override
    public Set<RevInfo> getJiraSet(String packagePath, List<Integer> lineNumbers) {
        return blameCache.computeIfAbsent(packagePath, k -> {
            try {
                return super.getJiraSet(packagePath, lineNumbers);
            } catch (SourceControlException e) {
                throw new SourceControlRuntimeException(e);
            }
        });
    }

    public static void clearCache() {
        changesCache.clear();
        blameCache.clear();
    }
}
