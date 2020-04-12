package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.resolvers.cli.GitDiffRunner;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.exception.SourceControlRuntimeException;
import com.sandboni.core.scm.proxy.SourceControlFilter;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.scope.*;
import com.sandboni.core.scm.utils.RawUtil;
import com.sandboni.core.scm.utils.ThrowingConsumer;
import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import io.reflectoring.diffparser.api.model.Range;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class CliChangeScopeResolver extends JGitChangeScopeResolver implements ChangeScopeResolver {

    final static String NON_EXISTING_FILE = "/dev/null";
    private final static Function<String, String> trimFileName = t -> t.startsWith("a/") || t.startsWith("b/") ? t.substring(2) : t;
    private final static Function<RawText, String> rawTextToString = t -> t.getString(0, t.size(), false);

    private static final Logger log = LoggerFactory.getLogger(CliChangeScopeResolver.class);

    public CliChangeScopeResolver(Repository repository, SourceControlFilter... filters) {
        super(repository, filters);
    }

    private static <T> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception ex) {
                throw new SourceControlRuntimeException("Unable to process diff", ex);
            }
        };
    }

    @Override
    public ChangeScope<Change> getChangeScope(RevisionScope<ObjectId> revisionScope) throws SourceControlException {
        final ChangeScope<Change> scope = new ChangeScopeImpl();
        List<Diff> diffs = GitDiffRunner.diff(repository, revisionScope);
        ObjectId toRevision = revisionScope.getTo();
        diffs.forEach(throwingConsumerWrapper(d -> getChangesForDiff(d, scope, toRevision)));
        return scope;
    }


    private void getChangesForDiff(Diff diff, ChangeScope<Change> scope, ObjectId toRevision) throws IOException {

        String fromFileName = trim(diff.getFromFileName());
        String toFileName = trim(diff.getToFileName());

        log.debug("processing diff: from file: {} ,to file {}", fromFileName, toFileName);

        if (!fileDeleted(toFileName)) {
            String content = getFileContent(toRevision, toFileName);
            String fqnFileName = RawUtil.getFullClassPath(content, toFileName);

            diff.getHunks().forEach(h -> processChanges(getChangesFromHunk(h), fqnFileName, content).forEach(scope::addChange));
        }
    }


    private String getFileContent(ObjectId revision, String fileName) throws IOException {
        return rawTextToString.apply(getRawText(revision, fileName));
    }

    private String trim(String fromFileName) {
        return trimFileName.apply(fromFileName);
    }

    private boolean fileDeleted(String toFileName) {
        return toFileName.equals(NON_EXISTING_FILE);
    }

    private Stream<Change> processChanges(Map<ChangeType, Set<Integer>> changes, String filePath, String content) {
        return changes.entrySet().parallelStream().map(e -> new SCMChangeBuilder().with(scm -> {
            scm.path = filePath;
            scm.changedLines = e.getValue();
            scm.changeType = e.getKey();
            scm.fileContent = content;
            scm.repository = repository.getWorkTree().getAbsolutePath();
        }).build());
    }

    private Map<ChangeType, Set<Integer>> getChangesFromHunk(Hunk hunk) {
        Range fromFileRange = hunk.getFromFileRange();
        int currentLine = Math.max(fromFileRange.getLineStart(), 1); // for new files, hunk line will start with 0
        List<Integer> pendingLines = new LinkedList<>();
        Map<ChangeType, Set<Integer>> changes = new HashMap<>();

        List<Line> lines = hunk.getLines();
        for (Line line : lines) {
            switch (line.getLineType()) {
                case NEUTRAL:
                    addAnyPendingLinesAsDeleted(changes, pendingLines);
                    currentLine++;
                    break;
                case FROM:
                    pendingLines.add(currentLine);
                    currentLine++;
                    break;
                case TO:
                    if (pendingLines.isEmpty()) {
                        // add
                        addChange(changes, ChangeType.ADD, currentLine);
                        currentLine++;
                    } else {
                        // modify
                        addChange(changes, ChangeType.MODIFY, pendingLines.remove(0));
                    }
            }
        }
        addAnyPendingLinesAsDeleted(changes, pendingLines);
        return changes;
    }

    private void addAnyPendingLinesAsDeleted(Map<ChangeType, Set<Integer>> changes, List<Integer> pendingLines) {
        if (!pendingLines.isEmpty()) {
            // delete
            addChanges(changes, ChangeType.DELETE, pendingLines);
            pendingLines.clear();
        }
    }

    private void addChange(Map<ChangeType, Set<Integer>> changes, ChangeType changeType, Integer lineNumber) {
        addChanges(changes, changeType, Collections.singletonList(lineNumber));
    }

    private void addChanges(Map<ChangeType, Set<Integer>> changes, ChangeType changeType, List<Integer> lineNumbers) {
        Set<Integer> currentOrInitSet = changes.getOrDefault(changeType, new HashSet<>());
        currentOrInitSet.addAll(lineNumbers);
        changes.put(changeType, currentOrInitSet);
    }


}
