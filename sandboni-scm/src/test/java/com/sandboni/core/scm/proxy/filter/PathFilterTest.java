package com.sandboni.core.scm.proxy.filter;

import com.sandboni.core.scm.GitInterface;
import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.utils.GitHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PathFilterTest {
    @Test
    public void diffWithPathFilter() throws SourceControlException, IOException {
        //there are 43 files diff-ed, but only 1 in 'src/test/resources' folder
        GitInterface diff = new GitRepository(GitHelper.openCurrentFolder(), new PathFilter("src/test/resources/"));

        ChangeScope<Change> changes = diff.getChanges("f88b40551e75f82e50ff35011d446577faf22523", "c97eb889d19acc254ea0da5886987eb5167ea459");

        changes.getAllAffectedClasses().forEach(System.out::println);
        Assert.assertEquals(1, changes.getAllAffectedClasses().size());
        Assert.assertTrue(changes.getAllAffectedClasses().contains("resources/test.properties"));
    }
}