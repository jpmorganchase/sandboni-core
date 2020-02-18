package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.revision.DiffConstants;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.utils.GitHelper;
import com.sandboni.core.scm.utils.PorcelainApi;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.util.Collections.singleton;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PorcelainApi.class)
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
        RevisionScope<ObjectId> scope = revisionResolver.resolve("fc776fe5e50", "e6f7c3d2954d6");
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);
        assertNotNull(changeScope);
        assertTrue(changeScope.getAllAffectedClasses().size() > 0);
    }

    @Test
    public void testComputeChangeScopeOfLocal() throws SourceControlException {
        Status status = mock(Status.class);
        when(status.hasUncommittedChanges()).thenReturn(true);
        when(status.getModified()).thenReturn(singleton("scm/src/main/java/com/sandboni/core/scm/resolvers/ChangeScopeResolver.java"));
        when(status.getChanged()).thenReturn(singleton("scm/src/main/java/com/sandboni/core/scm/resolvers/RevisionResolver.java"));
        when(status.getAdded()).thenReturn(singleton("scm/src/main/java/com/sandboni/core/scm/resolvers/BlameResolver.java"));

        mockStatic(PorcelainApi.class);
        when(PorcelainApi.call(any(), any())).thenReturn(status);

        RevisionScope<ObjectId> scope = revisionResolver.resolve(DiffConstants.LATEST_COMMIT, DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);
        assertNotNull(changeScope);
    }

    @Test
    public void testComputeChangeScopeOfLocalWithSingleChange() throws SourceControlException {
        Status status = mock(Status.class);
        when(status.hasUncommittedChanges()).thenReturn(true);
        when(status.getModified()).thenReturn(singleton("scm/src/main/java/com/sandboni/core/scm/resolvers/ChangeScopeResolver.java"));

        mockStatic(PorcelainApi.class);
        when(PorcelainApi.call(any(), any())).thenReturn(status);

        RevisionScope<ObjectId> scope = revisionResolver.resolve(DiffConstants.LATEST_COMMIT, DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);
        assertNotNull(changeScope);
    }
}
