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
        GitInterface diff = new GitRepository(GitHelper.openCurrentFolder()
                , new PathFilter("engine/src/test/resources/")
        );

        ChangeScope<Change> changes = diff.getChanges("34f5ecc6a5c", "3bbbf35036e3");

        changes.getAllAffectedClasses().forEach(System.out::println);
        Assert.assertTrue(changes.getAllAffectedClasses().contains("engine/resources/test.properties"));
    }
}