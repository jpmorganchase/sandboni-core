package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.exception.SourceControlRuntimeException;
import com.sandboni.core.scm.revision.DiffConstants;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.utils.GitHelper;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
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
    public void testFromAndToAreSame() throws SourceControlException {
        revisionResolver.resolve(DiffConstants.LATEST_COMMIT, DiffConstants.LATEST_COMMIT);
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

//    @Test
//    public void testTagAndUncommitted() throws SourceControlException {
//        RevisionScope<ObjectId> scope = revisionResolver.resolve("refs/remotes/origin/" + Constants.MASTER, DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
//        assertNotNull(scope);
//        assertNotNull(scope.getFrom());
//        assertNotEquals(ObjectId.zeroId(), scope.getFrom());
//        assertNotNull(scope.getTo());
//        assertEquals(ObjectId.zeroId(), scope.getTo());
//    }

    @Test
    public void testCommitAndUncommitted() throws SourceControlException {
        RevisionScope<ObjectId> scope = revisionResolver.resolve("34f5ecc6a5ca2ed8877dde8afe8aaeef7ebf0248", DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
        assertNotNull(scope);
        assertNotNull(scope.getFrom());
        assertNotEquals(ObjectId.zeroId(), scope.getFrom());
        assertTrue(scope.getFrom().toString().contains("34f5ecc6a5ca2ed8877dde8afe8aaeef7ebf0248"));
        assertNotNull(scope.getTo());
        assertEquals(ObjectId.zeroId(), scope.getTo());
    }
}
