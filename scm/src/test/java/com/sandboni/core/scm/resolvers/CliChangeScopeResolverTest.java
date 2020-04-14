package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.resolvers.cli.GitDiffRunner;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.exception.SourceControlRuntimeException;
import com.sandboni.core.scm.revision.DiffConstants;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.scope.ChangeType;
import com.sandboni.core.scm.utils.GitHelper;
import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import io.reflectoring.diffparser.api.model.Range;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.sandboni.core.scm.resolvers.CliChangeScopeResolver.NON_EXISTING_FILE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GitDiffRunner.class)
public class CliChangeScopeResolverTest {
    private RevisionResolver revisionResolver;
    private CliChangeScopeResolver changeScopeResolver;

    @Before
    public void before() {
        Repository repository = GitRepository.buildRepository(GitHelper.openCurrentFolder());
        revisionResolver = new RevisionResolver(repository);
        changeScopeResolver = new CliChangeScopeResolver(repository);
    }

    @Test
    public void testComputeChangeScopeFromGitDiffOutput_deletedFile() throws SourceControlException {
        mockStatic(GitDiffRunner.class);

        Diff diff = getChangeScopeDeletedFile();

        when(GitDiffRunner.diff(any(), any())).thenReturn(singletonList(diff));

        RevisionScope<ObjectId> scope = revisionResolver.resolve("fc776fe5e50", "e6f7c3d2954d6");
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);

        assertNotNull(changeScope);
        assertTrue(changeScope.getAllAffectedClasses().isEmpty());
    }

    @Test
    public void testComputeChangeScopeFromGitDiffOutput_OneDiffOneHunk() throws SourceControlException {
        mockStatic(GitDiffRunner.class);

        Diff diff = getChangeScopeResolverDiff();

        when(GitDiffRunner.diff(any(), any())).thenReturn(singletonList(diff));

        RevisionScope<ObjectId> scope = revisionResolver.resolve("fc776fe5e50", "e6f7c3d2954d6");
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);

        String fileName = "com/sandboni/core/scm/resolvers/ChangeScopeResolver.java";
        assertNotNull(changeScope);
        List<Change> changes = changeScope.getChanges(fileName);
        assertNotNull(changes);
        assertEquals(1, changes.size());
        Change change = changes.get(0);
        assertEquals(fileName, change.getFilename());
        assertEquals(ChangeType.DELETE, change.getType());
        Set<Integer> linesChanged = change.getLinesChanged();
        assertNotNull(linesChanged);
        assertEquals(2, linesChanged.size());
        assertTrue(linesChanged.contains(10));
        assertTrue(linesChanged.contains(11));
    }

    @Test
    public void testComputeChangeScopeOfCliGitDiffOutput_nonExistingChanges() throws SourceControlException {
        mockStatic(GitDiffRunner.class);

        Diff diff = getChangeScopeResolverDiff();

        when(GitDiffRunner.diff(any(), any())).thenReturn(singletonList(diff));

        RevisionScope<ObjectId> scope = revisionResolver.resolve(DiffConstants.LATEST_COMMIT, DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);

        String fileName = "com/sandboni/core/scm/resolvers/SomeNonExistingChangeScopeResolver.java";
        assertNotNull(changeScope);
        List<Change> changes = changeScope.getChanges(fileName);
        assertNull(changes);
    }

    @Test(expected = SourceControlRuntimeException.class)
    public void testComputeChangeScopeOfCliGitDiffOutput_nonExistingFile() throws SourceControlException {
        mockStatic(GitDiffRunner.class);

        Diff diff = getChangeScopeNonExistingFile();

        when(GitDiffRunner.diff(any(), any())).thenReturn(singletonList(diff));

        RevisionScope<ObjectId> scope = revisionResolver.resolve(DiffConstants.LATEST_COMMIT, DiffConstants.LOCAL_CHANGES_NOT_COMMITTED);
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);

        assertTrue(true);
    }

    @Test
    public void testComputeChangeScopeOfCliGitDiffOutput_TwoDiffsMultipleHunks() throws SourceControlException {
        mockStatic(GitDiffRunner.class);

        Diff diff1 = getChangeScopeResolverDiff();
        Diff diff2 = getProcessorDiff();

        when(GitDiffRunner.diff(any(), any())).thenReturn(Arrays.asList(diff1, diff2));

        RevisionScope<ObjectId> scope = revisionResolver.resolve("fc776fe5e50", "e6f7c3d2954d6");
        ChangeScope<Change> changeScope = changeScopeResolver.getChangeScope(scope);


        String fileName1 = "com/sandboni/core/scm/resolvers/ChangeScopeResolver.java";
        String fileName2 = "com/sandboni/core/engine/Processor.java";

        assertNotNull(changeScope);
        List<Change> changes1 = changeScope.getChanges(fileName1);
        assertNotNull(changes1);
        assertEquals(1, changes1.size());

        List<Change> changes2 = changeScope.getChanges(fileName2);
        assertNotNull(changes2);
        assertEquals(3, changes2.size());

        Change added = changes2.get(0);
        assertEquals(ChangeType.ADD, added.getType());
        assertEquals(4, added.getLinesChanged().size());

        Change deleted = changes2.get(1);
        assertEquals(ChangeType.DELETE, deleted.getType());
        assertEquals(3, deleted.getLinesChanged().size());

        Change modified = changes2.get(2);
        assertEquals(ChangeType.MODIFY, modified.getType());
        assertEquals(2, modified.getLinesChanged().size());

    }



    // 1 diff, 4 hunks
    private Diff getProcessorDiff() {
        String fileName = "com/sandboni/core/engine/Processor.java";
        String pkgLocation = "engine/src/main/java/";

        List<Line> hunk1Lines = new ArrayList<>();
        hunk1Lines.add(new Line(Line.LineType.NEUTRAL, ""));
        hunk1Lines.add(new Line(Line.LineType.TO, "+    private boolean addedMethod() {"));
        hunk1Lines.add(new Line(Line.LineType.TO, "+        return true;"));
        hunk1Lines.add(new Line(Line.LineType.TO, "+    }"));
        hunk1Lines.add(new Line(Line.LineType.TO, ""));
        hunk1Lines.add(new Line(Line.LineType.NEUTRAL, "     Collection<Connector> getConnectors() {"));
        Hunk hunk1 = createHunk(hunk1Lines, 72, 2, 72, 6);

        List<Line> hunk2Lines = new ArrayList<>();
        hunk2Lines.add(new Line(Line.LineType.NEUTRAL, " "));
        hunk2Lines.add(new Line(Line.LineType.FROM, "-    private boolean isIntegrationStage() {"));
        hunk2Lines.add(new Line(Line.LineType.FROM, "-        return arguments.getStage().equals(Stage.INTEGRATION.name());"));
        hunk2Lines.add(new Line(Line.LineType.FROM, "-    }"));
        hunk2Lines.add(new Line(Line.LineType.NEUTRAL, ""));
        Hunk hunk2 = createHunk(hunk2Lines, 122, 5, 126, 2);

        List<Line> hunk3Lines = new ArrayList<>();
        hunk3Lines.add(new Line(Line.LineType.NEUTRAL, "         log.info(\"[{}] Getting change scope\", Thread.currentThread().getName());"));
        hunk3Lines.add(new Line(Line.LineType.FROM, "-        long start = System.nanoTime();"));
        hunk3Lines.add(new Line(Line.LineType.TO, "+        long start = System.nanoTime()+100;"));
        hunk3Lines.add(new Line(Line.LineType.NEUTRAL, "         try {"));
        Hunk hunk3 = createHunk(hunk3Lines, 128, 3, 128, 3);

        List<Line> hunk4Lines = new ArrayList<>();
        hunk4Lines.add(new Line(Line.LineType.NEUTRAL, "             return new Builder(context, FilterIndicator.NONE);"));
        hunk4Lines.add(new Line(Line.LineType.FROM, "-        } else if (proceed(context.getChangeScope())) {"));
        hunk4Lines.add(new Line(Line.LineType.TO, "+        } else if (proceed(context.getChangeScope()) && someNewCheck()) {"));
        hunk4Lines.add(new Line(Line.LineType.NEUTRAL, "             log.info(\"Found changes: {}\", context.getChangeScope());"));
        Hunk hunk4 = createHunk(hunk4Lines, 149, 3, 149, 3);
        List<Hunk> hunks = Arrays.asList(hunk1, hunk2, hunk3, hunk4);

        String fromFileName = "a/" + pkgLocation + fileName;
        String toFileName = "b/" + pkgLocation + fileName;
        List<String> header = Arrays.asList("diff1 --git " + fromFileName + " " + toFileName,
            "index e5e7b8e..47f358a 100644",
            "--- " + fromFileName,
            "+++ " + toFileName);
        return createDiff(fromFileName, toFileName, header, hunks);
    }

    private Diff getChangeScopeDeletedFile() {
        String fileName = "com/sandboni/core/scm/DeletedClass.java";
        String pkgLocation = "scm/src/main/java/";

        List<Line> hunkLines = new ArrayList<>();
        hunkLines.add(new Line(Line.LineType.FROM, "-package com.jpmchase.sdm.common;"));
        hunkLines.add(new Line(Line.LineType.FROM, "-"));
        hunkLines.add(new Line(Line.LineType.FROM, "-import com.jpmchase.sdm.exception.InvalidGenericTypeError;"));
        hunkLines.add(new Line(Line.LineType.FROM, "-"));
        hunkLines.add(new Line(Line.LineType.FROM, "-public class TypeSafeEnum extends GenericType {"));
        hunkLines.add(new Line(Line.LineType.FROM, "-"));
        hunkLines.add(new Line(Line.LineType.FROM, "-       public final String getDescription() throws InvalidGenericTypeError {"));
        hunkLines.add(new Line(Line.LineType.FROM, "-               //TODO"));
        hunkLines.add(new Line(Line.LineType.FROM, "-               return null;"));
        hunkLines.add(new Line(Line.LineType.FROM, "-       }"));
        hunkLines.add(new Line(Line.LineType.FROM, "-"));
        hunkLines.add(new Line(Line.LineType.FROM, "-}"));
        Hunk hunk = createHunk(hunkLines, 1, 12, 0, 0);
        List<Hunk> hunks = singletonList(hunk);

        String fromFileName = "a/" + pkgLocation + fileName;
        String toFileName = "b/" + pkgLocation + fileName;
            List<String> header = Arrays.asList(
                "deleted file mode 100644",
                "index 47fe940..f0d9cd3 100644",
            "--- " + fromFileName,
            "+++ " + NON_EXISTING_FILE);
        return createDiff(fromFileName, NON_EXISTING_FILE, header, hunks);
    }

    private Diff getChangeScopeNonExistingFile() {
        String fileName = "com/sandboni/core/scm/NonExistingdClass.java";
        String pkgLocation = "scm/src/main/java/";

        List<Line> hunkLines = new ArrayList<>();
        hunkLines.add(new Line(Line.LineType.FROM, "-package com.jpmchase.sdm.common;"));
        Hunk hunk = createHunk(hunkLines, 1, 1, 1, 1);
        List<Hunk> hunks = singletonList(hunk);

        String fromFileName = "a/" + pkgLocation + fileName;
        String toFileName = "b/" + pkgLocation + fileName;
            List<String> header = Arrays.asList(
                "deleted file mode 100644",
                "index 47fe940..f0d9cd3 100644",
            "--- " + fromFileName,
            "+++ " + toFileName);
        return createDiff(fromFileName, toFileName, header, hunks);
    }

    // 1 diff, 1 hunk
    private Diff getChangeScopeResolverDiff() {
        String fileName = "com/sandboni/core/scm/resolvers/ChangeScopeResolver.java";
        String pkgLocation = "scm/src/main/java/";

        List<Line> hunkLines = new ArrayList<>();
        hunkLines.add(new Line(Line.LineType.NEUTRAL, " import com.sandboni.core.scm.utils.RawUtil;"));
        hunkLines.add(new Line(Line.LineType.FROM, "-import com.sandboni.core.scm.utils.timing.StopWatch;"));
        hunkLines.add(new Line(Line.LineType.FROM, "-import com.sandboni.core.scm.utils.timing.StopWatchManager;"));
        hunkLines.add(new Line(Line.LineType.NEUTRAL, " import org.eclipse.jgit.api.Status;"));
        Hunk hunk = createHunk(hunkLines, 9, 4, 9, 2);
        List<Hunk> hunks = singletonList(hunk);

        String fromFileName = "a/" + pkgLocation + fileName;
        String toFileName = "b/" + pkgLocation + fileName;
        List<String> header = Arrays.asList(
            "index 47fe940..f0d9cd3 100644",
            "--- " + fromFileName,
            "+++ " + toFileName);
        return createDiff(fromFileName, toFileName, header, hunks);
    }

    private Diff createDiff(String fromFileName, String toFileName, List<String> header, List<Hunk> hunks) {
        Diff diff = new Diff();
        diff.setFromFileName(fromFileName);
        diff.setToFileName(toFileName);
        diff.setHeaderLines(header);
        diff.setHunks(hunks);
        return diff;
    }

    private Hunk createHunk(List<Line> hunkLines, int fromLineStart, int fromLineCount, int toLineStart, int toLineCount) {
        Hunk hunk = new Hunk();
        hunk.setFromFileRange(new Range(fromLineStart, fromLineCount));
        hunk.setToFileRange(new Range(toLineStart, toLineCount));
        hunk.setLines(hunkLines);
        return hunk;
    }

}