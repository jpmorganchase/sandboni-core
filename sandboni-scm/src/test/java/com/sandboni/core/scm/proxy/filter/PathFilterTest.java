package com.sandboni.core.scm.proxy.filter;

import com.sandboni.core.scm.GitInterface;
import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.revision.DiffConstants;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.utils.GitHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PathFilterTest {
    @Test
    public void diffWithPathFilter() throws SourceControlException {
        GitInterface diff = new GitRepository(GitHelper.openCurrentFolder(), new PathFilter("sandboni-engine/src/test/resources/"));

        ChangeScope<Change> changes = diff.getChanges("0b345a12", "60b497fb");

        changes.getAllAffectedClasses().forEach(System.out::println);
        Assert.assertTrue(changes.getAllAffectedClasses().contains("sandboni-engine/src/test/resources/test.properties"));
    }
}