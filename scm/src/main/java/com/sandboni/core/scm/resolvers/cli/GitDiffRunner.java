package com.sandboni.core.scm.resolvers.cli;

import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.proxy.filter.FileExtensions;
import com.sandboni.core.scm.revision.RevisionScope;
import io.reflectoring.diffparser.api.UnifiedDiffParser;
import io.reflectoring.diffparser.api.model.Diff;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GitDiffRunner {

    private static final String NEW_DIFF_PREFIX = "diff --git";

    private GitDiffRunner() {
    }

    public static List<Diff> diff(Repository repository, RevisionScope<ObjectId> revisionScope) throws SourceControlException {
        String[] command = buildDiffCommand(revisionScope);
        try {
            List<String> diffLines = ProcessRunner.runCommand(getRepositoryBaseFolder(repository), command);
            byte[] diffBytes = pad(diffLines); // workaround for https://github.com/thombergs/diffparser/issues/31
            return new UnifiedDiffParser().parse(diffBytes);
        } catch (IOException e) {
            throw new SourceControlException("Error running git diff command", e);
        }
    }

    /**
     * UnifiedDiffParse assumes empty lfoine between diffs. we don't always get that.
     *
     * @param diffLines output from git diff run
     * @return padded output
     */
    private static byte[] pad(List<String> diffLines) {
        StringBuilder padded = new StringBuilder();
        String prevLine = null;
        for (String line : diffLines) {
            if (isNewDiff(line) && StringUtils.isNotEmpty(prevLine)) {
                padded.append(System.lineSeparator());
            }
            padded.append(line).append(System.lineSeparator());
            prevLine = line;
        }
        return padded.toString().getBytes();
    }

    private static File getRepositoryBaseFolder(Repository repository) {
        return Paths.get(repository.getDirectory().getAbsolutePath(), "/..").toFile();
    }

    private static boolean isNewDiff(String line) {
        return line.startsWith(NEW_DIFF_PREFIX);
    }

    private static String[] buildDiffCommand(RevisionScope<ObjectId> revisionScope) {
        ObjectId fromRevision = revisionScope.getFrom();
        ObjectId toRevision = revisionScope.getTo();

        List<String> command = new ArrayList<>();

        command.add("git");
        command.add("diff");
        command.add("-U1");
        command.add(fromRevision.getName());
        if (!toRevision.equals(ObjectId.zeroId())) {
            command.add(toRevision.getName());
        }
        // filter only ".java" and ".feature" files
        command.add("--");
        command.add("\"**/*" + FileExtensions.JAVA.extension() + "\"");
        command.add("\"**/*" + FileExtensions.FEATURE.extension() + "\"");

        return command.toArray(new String[0]);
    }


}
