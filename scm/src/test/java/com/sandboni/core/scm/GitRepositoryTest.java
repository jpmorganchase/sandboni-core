package com.sandboni.core.scm;

import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.exception.SourceControlRuntimeException;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.utils.GitHelper;
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
}
