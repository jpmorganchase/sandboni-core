package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.utils.GitHelper;
import org.eclipse.jgit.lib.Constants;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TreeWalkResolverTest {
    private TreeWalkResolver treeWalkResolver;

    @Before
    public void before() {
        treeWalkResolver = new TreeWalkResolver(GitRepository.buildRepository(GitHelper.openCurrentFolder()), Constants.HEAD);
    }

    @Test
    public void testFullPath() throws IOException {
        String filepath = "com/sandboni/core/engine/Application.java";
        String fullPath = treeWalkResolver.getFullPath(filepath);
        assertNotNull(fullPath);
        assertEquals("sandboni-engine/src/main/java/com/sandboni/core/engine/Application.java", fullPath);
    }

    @Test
    public void testNonExistingPath() throws IOException {
        String filepath = "com/sandboni/core/engine/ApplicationAAaa.java";
        String fullPath = treeWalkResolver.getFullPath(filepath);
        assertNull(fullPath);
    }
}
