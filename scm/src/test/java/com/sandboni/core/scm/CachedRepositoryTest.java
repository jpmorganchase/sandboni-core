package com.sandboni.core.scm;

import com.sandboni.core.scm.exception.SourceControlException;
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

public class CachedRepositoryTest {
    private GitInterface gitRepository;

    @Before
    public void before() {
        gitRepository = new CachedRepository(GitHelper.openCurrentFolder());
    }

    @Test
    public void testGetChanges() throws SourceControlException {
        ChangeScope<Change> changeScope = gitRepository.getChanges("0b345a1258dfa", "60b497fb350a7");
        ChangeScope<Change> changeScope2 = gitRepository.getChanges("0b345a1258dfa", "60b497fb350a7");
        assertEquals(changeScope, changeScope2);
    }

    @Test
    public void testGetJiraSet() throws SourceControlException {
        List<Integer> list = Arrays.asList(1, 2, 8, 9, 10, 100, 9999999);
        String filepath = "sandboni-engine/src/main/java/com/sandboni/core/Application.java";
        Set<RevInfo> jiraSet = gitRepository.getJiraSet(filepath, list);
        Set<RevInfo> jiraSet2 = gitRepository.getJiraSet(filepath, list);
        assertEquals(jiraSet, jiraSet2);
    }
}
