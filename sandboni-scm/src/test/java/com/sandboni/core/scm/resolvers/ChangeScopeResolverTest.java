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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        RevisionScope<ObjectId> scope = revisionResolver.resolve("87578ac4920", "253659759ac");
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);
        assertNotNull(changeScope);
        assertEquals(89, changeScope.getAllAffectedClasses().size());
    }

    @Test
    public void testComputeChangeScopeOfLocal() throws SourceControlException {
        RevisionScope<ObjectId> scope = revisionResolver.resolve(DiffConstants.LATEST_COMMIT, DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);
        assertNotNull(changeScope);
    }
}
