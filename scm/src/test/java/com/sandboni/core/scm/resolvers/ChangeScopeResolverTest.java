package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.revision.DiffConstants;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.utils.GitHelper;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChangeScopeResolverTest {
    private RevisionResolver revisionResolver;
    private ChangeScopeResolver changeScopeResolver;

    @Before
    public void before() {
        Repository repository = GitRepository.buildRepository(GitHelper.openCurrentFolder());
        revisionResolver = new RevisionResolver(repository);
        changeScopeResolver = new ChangeScopeResolver(repository);
    }

    @Test
    public void testComputeChangeScope() throws SourceControlException {
        RevisionScope<ObjectId> scope = revisionResolver.resolve("d446122593b", "2f9b1266eb9f3882bdf");
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);
        assertNotNull(changeScope);
        assertTrue(changeScope.getAllAffectedClasses().size() > 0);
    }

    @Test
    public void testComputeChangeScopeOfLocal() throws SourceControlException {
        RevisionScope<ObjectId> scope = revisionResolver.resolve(DiffConstants.LATEST_COMMIT, DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);
        assertNotNull(changeScope);
    }
}
