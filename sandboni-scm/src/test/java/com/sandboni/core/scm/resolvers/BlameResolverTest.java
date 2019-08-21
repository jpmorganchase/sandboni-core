package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.utils.GitHelper;
import org.eclipse.jgit.lib.Constants;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class BlameResolverTest {
    private BlameResolver blameResolver;

    @Before
    public void before() {
        blameResolver = new BlameResolver(GitRepository.buildRepository(GitHelper.openCurrentFolder()), Constants.HEAD);
    }

    @Test
    public void testBlameFile() throws SourceControlException {
        List<Integer> list = Arrays.asList(1, 2, 8, 9, 10, 100, 9999999);
        String filepath = "sandboni-engine/src/main/java/com/sandboni/core/engine/Application.java";

        Set<RevInfo> result = blameResolver.blame(filepath, list);
        assertNotNull(result);
        assertEquals(6, result.size());
        assertTrue(result.stream().allMatch(r -> r.getJiraId().startsWith("SANDBONI-")));
        assertTrue(result.stream().allMatch(r -> r.getRevisionId() != null));
        assertTrue(result.stream().allMatch(r -> r.getDate() != null));
    }

    @Test
    public void testBlameNonExistingFile() throws SourceControlException {
        List<Integer> list = Arrays.asList(1, 2, 8, 9);
        String filepath = "sandboni-engine/src/main/java/com/sandboni/core/engine/ApplicationAAAAAAAAAa.java";

        Set<RevInfo> result = blameResolver.blame(filepath, list);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
