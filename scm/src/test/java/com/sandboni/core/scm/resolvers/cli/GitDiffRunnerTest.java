package com.sandboni.core.scm.resolvers.cli;

import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.proxy.filter.FileExtensions;
import com.sandboni.core.scm.utils.ResourceFileUtils;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.resolvers.RevisionResolver;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.utils.GitHelper;
import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProcessRunner.class, GitDiffRunner.class})
public class GitDiffRunnerTest {
    private Repository repository;
    private RevisionResolver revisionResolver;

    @Before
    public void setup() {
        repository = GitRepository.buildRepository(GitHelper.openCurrentFolder());
        revisionResolver = new RevisionResolver(repository);
        PowerMockito.spy(GitDiffRunner.class);
    }

    @Test
    public void testDiff() throws Exception {
        // adding -- "/*.java" "/*.feature" fails on travis build. Not sure the cause
        PowerMockito.doNothing().when(GitDiffRunner.class, "addFileTypeFilters", Mockito.any());

        RevisionScope<ObjectId> scope = revisionResolver.resolve("ec302a13", "60075ec4"); // any
        List<Diff> diffs = GitDiffRunner.diff(repository, scope);

        assertNotNull(diffs);
        assertEquals(33, diffs.size());

        // deleted file
        List<Diff> cbfDiffs = findDiff(diffs, "CachedBcelFinder.java");
        assertEquals(1, cbfDiffs.size());
        Diff cbfDiff = cbfDiffs.get(0);
        assertEquals("/dev/null", cbfDiff.getToFileName());
        List<Hunk> cbfHunks = cbfDiff.getHunks();
        assertEquals(1, cbfHunks.size());
        assertEquals(23, cbfHunks.get(0).getLines().size());

        // moved file
        List<Diff> jfDiffs = findDiff(diffs, "JarFinder.java");
        assertEquals(1, jfDiffs.size());
        Diff jfDiff = jfDiffs.get(0);
        assertEquals("a/engine/src/main/java/com/sandboni/core/engine/finder/JarFinder.java", jfDiff.getFromFileName());
        assertEquals("b/engine/src/main/java/com/sandboni/core/engine/finder/jar/JarFinder.java", jfDiff.getToFileName());
        List<Hunk> jfHunks = jfDiff.getHunks();
        assertEquals(5, jfHunks.size());

        // added file
        List<Diff> dsDiffs = findDiff(diffs, "DirectoryScanner.java");
        assertEquals(1, dsDiffs.size());
        Diff dsDiff = dsDiffs.get(0);
        assertEquals("/dev/null", dsDiff.getFromFileName());
        List<Hunk> dsHunks = dsDiff.getHunks();
        assertEquals(1, dsHunks.size());
        assertEquals(55, dsHunks.get(0).getLines().size());

        // modified file (added, modified and deleted lines)
        List<Diff> feDiffs = findDiff(diffs, "FinderExecutor.java");
        assertEquals(1, feDiffs.size());
        Diff feDiff = feDiffs.get(0);
        List<Hunk> feHunks = feDiff.getHunks();
        assertEquals(3, feHunks.size());

        // removed line
        Hunk feHunk1 = feHunks.get(0);
        assertEquals(4, feHunk1.getFromFileRange().getLineStart());
        assertEquals(3, feHunk1.getLines().size());
        assertEquals(Line.LineType.NEUTRAL, feHunk1.getLines().get(0).getLineType());
        assertEquals(Line.LineType.FROM, feHunk1.getLines().get(1).getLineType());
        assertEquals(Line.LineType.NEUTRAL, feHunk1.getLines().get(2).getLineType());

        // modified line
        Hunk feHunk2 = feHunks.get(1);
        assertEquals(21, feHunk2.getFromFileRange().getLineStart());
        assertEquals(4, feHunk2.getLines().size());
        assertEquals(Line.LineType.NEUTRAL, feHunk2.getLines().get(0).getLineType());
        assertEquals(Line.LineType.FROM, feHunk2.getLines().get(1).getLineType());
        assertEquals(Line.LineType.TO, feHunk2.getLines().get(2).getLineType());
        assertEquals(Line.LineType.NEUTRAL, feHunk2.getLines().get(3).getLineType());

        // added lines
        Hunk feHunk3 = feHunks.get(2);
        assertEquals(25, feHunk3.getFromFileRange().getLineStart());
        assertEquals(7, feHunk3.getLines().size());
        assertEquals(2, feHunk3.getLines().stream().filter(l -> l.getLineType().equals(Line.LineType.NEUTRAL)).count());
        assertEquals(5, feHunk3.getLines().stream().filter(l -> l.getLineType().equals(Line.LineType.TO)).count());

    }

    private List<Diff> findDiff(List<Diff> diffs, String fileName) {
        return diffs.stream().filter(d -> d.getFromFileName().contains(fileName) || d.getToFileName().contains(fileName)).collect(Collectors.toList());
    }

    @Test(expected = SourceControlException.class)
    public void testDiff_invalidDiffResult() throws IOException, SourceControlException {
        mockStatic(ProcessRunner.class);

        List<String> diffLines = ResourceFileUtils.getResourceFileContentAsList(getClass(), "gitDiffInvalid.out");

        when(ProcessRunner.runCommand(any(), any())).thenReturn(diffLines);

        RevisionScope<ObjectId> scope = revisionResolver.resolve("44444444", "55555555");
        GitDiffRunner.diff(repository, scope);

        assertTrue(true);
    }

    @Test
    public void testDiffWithSameToAndFrom() throws Exception {
        RevisionScope<ObjectId> scope = revisionResolver.resolve("60075ec4", "60075ec4"); // same
        PowerMockito.doNothing().when(GitDiffRunner.class, "addFileTypeFilters", Mockito.any());
        List<Diff> diffs = GitDiffRunner.diff(repository, scope);
        assertNotNull(diffs);
        assertEquals(33, diffs.size());
    }

    @Test
    public void testGitDiffCommand_FileTypeFilterIsAdded() throws SourceControlException {
        RevisionScope<ObjectId> scope = revisionResolver.resolve("ec302a13", "ec302a13"); //any
        PowerMockito.when(GitDiffRunner.buildDiffCommand(scope)).thenCallRealMethod();
        String[] expectedCommand = GitDiffRunner.buildDiffCommand(scope);
        assertTrue(Arrays.asList(expectedCommand).contains("--"));
        assertTrue(Arrays.asList(expectedCommand).contains("\"**/*" + FileExtensions.JAVA.extension() + "\""));
        assertTrue(Arrays.asList(expectedCommand).contains("\"**/*" + FileExtensions.FEATURE.extension() + "\""));
    }
}