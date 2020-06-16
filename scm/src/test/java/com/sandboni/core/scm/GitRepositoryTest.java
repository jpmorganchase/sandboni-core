package com.sandboni.core.scm;

import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.exception.SourceControlRuntimeException;
import com.sandboni.core.scm.proxy.SourceControlFilter;
import com.sandboni.core.scm.resolvers.ChangeScopeResolver;
import com.sandboni.core.scm.resolvers.CliChangeScopeResolver;
import com.sandboni.core.scm.resolvers.JGitChangeScopeResolver;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.utils.GitHelper;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class GitRepositoryTest {
    private GitInterface gitRepository;

    @Before
    public void before() {
        gitRepository = new GitRepository(GitHelper.openCurrentFolder());
    }

    @Test(expected = SourceControlRuntimeException.class)
    public void testNonExistingRepository() {
        new GitRepository(System.getProperty("user.home"));
    }

    @Test
    public void testGetChanges() throws SourceControlException {
        ChangeScope<Change> changeScope = gitRepository.getChanges("fc776fe5e50", "e6f7c3d2954d6");
        assertNotNull(changeScope);
        assertTrue(changeScope.getAllAffectedClasses().size() > 0);
    }

    @Test
    public void testGetJiraSet() throws SourceControlException {
        List<Integer> list = Arrays.asList(1, 2, 8, 9, 10, 100, 9999999);
        String filepath = "engine/src/main/java/com/sandboni/core/engine/Application.java";
        Set<RevInfo> jiraSet = gitRepository.getJiraSet(filepath, list);
        assertNotNull(jiraSet);
        assertFalse(jiraSet.isEmpty());
        assertEquals(6, jiraSet.size());
    }

    @Test
    public void testGetJiraSetForNonExistingFile() throws SourceControlException {
        List<Integer> list = Arrays.asList(1, 2, 8, 9);
        String filepath = "ApplicationAaaa.java";
        Set<RevInfo> jiraSet = gitRepository.getJiraSet(filepath, list);
        assertNotNull(jiraSet);
        assertTrue(jiraSet.isEmpty());
    }

    @Test
    public void testGetChangeScopeResolver() {
        ObjectId from = ObjectId.fromString("0d96512867985be6c9ee555dbccd68d53374af0d"); // different from and to
        ObjectId to = ObjectId.fromString("9c83f948d27326fb3750afbc3d023876d9320482");
        SourceControlFilter[] finders = new SourceControlFilter[0];
        GitRepository gitRepository =  new GitRepository(GitHelper.openCurrentFolder(), finders);
        ChangeScopeResolver changeScopeResolver = gitRepository.getChangeScopeResolver(from, to);
        assertTrue(changeScopeResolver instanceof JGitChangeScopeResolver);
    }

    @Test
    public void testGetChangeScopeResolverSameObjectId() {
        ObjectId from = ObjectId.fromString("0d96512867985be6c9ee555dbccd68d53374af0d"); // same from and to
        ObjectId to = ObjectId.fromString("0d96512867985be6c9ee555dbccd68d53374af0d");
        SourceControlFilter[] finders = new SourceControlFilter[0];
        GitRepository gitRepository =  new GitRepository(GitHelper.openCurrentFolder(), finders);
        ChangeScopeResolver changeScopeResolver = gitRepository.getChangeScopeResolver(from, to);
        assertTrue(changeScopeResolver instanceof CliChangeScopeResolver);
    }
}
