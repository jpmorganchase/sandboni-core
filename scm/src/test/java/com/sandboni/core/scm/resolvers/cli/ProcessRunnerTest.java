package com.sandboni.core.scm.resolvers.cli;

import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.resolvers.cli.ProcessRunner;
import com.sandboni.core.scm.utils.GitHelper;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProcessRunnerTest {

    private static final String expectedDiffResult = "diff --git a/scm/src/test/java/com/sandboni/core/scm/proxy/filter/TestFile.java b/scm/src/test/java/com/sandboni/core/scm/proxy/filter/TestFile.java\n" +
        "new file mode 100644\n" +
        "index 0000000..fe3f603\n" +
        "--- /dev/null\n" +
        "+++ b/scm/src/test/java/com/sandboni/core/scm/proxy/filter/TestFile.java\n" +
        "@@ -0,0 +1,4 @@\n" +
        "+package com.sandboni.core.scm.proxy.filter;\n" +
        "+\n" +
        "+public class TestFile {\n" +
        "+}";

    @Test
    public void testRunCommand() throws IOException {
        String[] diffCmd = buildDiffCommand();

        Repository repository = GitRepository.buildRepository(GitHelper.openCurrentFolder());
        List<String> cmdResult = ProcessRunner.runCommand(repository.getDirectory().getParentFile(), diffCmd);
        String resultAsString = String.join("\n", cmdResult);

        assertEquals(expectedDiffResult, resultAsString);
    }

    @Test(expected = IOException.class)
    public void testRunCommand_invalidRevision() throws IOException {
        List<String> strings = ProcessRunner.runCommand(new File("./nonexistingfolder"), "git", "diff");
        assertTrue(true);
    }

    private static String[] buildDiffCommand() {
        return new String[] {"git", "diff", "-U1", "fc776fe5e50", "e6f7c3d2954d6"};
    }

}