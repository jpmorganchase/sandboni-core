package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.revision.DiffConstants;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.utils.GitHelper;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RevisionResolverTest {
    private RevisionResolver revisionResolver;

    @Before
    public void before() {
        revisionResolver = new RevisionResolver(GitRepository.buildRepository(GitHelper.openCurrentFolder()));
    }

    @Test(expected = SourceControlException.class)
    public void testFromIsNull() throws SourceControlException {
        revisionResolver.resolve(null, DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
    }

    @Test(expected = SourceControlException.class)
    public void testToIsNull() throws SourceControlException {
        revisionResolver.resolve(DiffConstants.LATEST_COMMIT, null);
    }

    @Test(expected = SourceControlException.class)
    public void testInvalidCommit() throws SourceControlException {
        revisionResolver.resolve("AAAAAA", DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
    }

    @Test
    public void testLatestPushAndUncommitted() throws SourceControlException {
        RevisionScope<ObjectId> scope = revisionResolver.resolve(DiffConstants.LATEST_PUSH, DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
        assertNotNull(scope);
        assertNotNull(scope.getFrom());
        assertNotEquals(ObjectId.zeroId(), scope.getFrom());
        assertNotNull(scope.getTo());
        assertEquals(ObjectId.zeroId(), scope.getTo());
    }

    @Test
    public void testCommitAndUncommitted() throws SourceControlException {
        RevisionScope<ObjectId> scope = revisionResolver.resolve("fc776fe5e50", DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
        assertNotNull(scope);
        assertNotNull(scope.getFrom());
        assertNotEquals(ObjectId.zeroId(), scope.getFrom());
        assertTrue(scope.getFrom().toString().contains("fc776fe5e50"));
        assertNotNull(scope.getTo());
        assertEquals(ObjectId.zeroId(), scope.getTo());
    }
}
