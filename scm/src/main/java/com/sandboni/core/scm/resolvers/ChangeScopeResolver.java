package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.exception.ErrorMessages;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.proxy.SourceControlFilter;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.scope.*;
import com.sandboni.core.scm.utils.PorcelainApi;
import com.sandboni.core.scm.utils.RawUtil;
import com.sandboni.core.scm.utils.timing.SWConsts;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.*;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChangeScopeResolver {
    private final Repository repository;
    private final DiffFormatter diffFormatter;

    public ChangeScopeResolver(Repository repository, SourceControlFilter... filters) {
        this.repository = repository;
        this.diffFormatter = new DiffFormatterBuilder().with(b -> {
            b.repository = repository;
            if (filters != null) {
                Arrays.stream(filters).forEach(f -> b.filters.addAll(f.getCriteria()));
            }
        }).build();
    }

    public ChangeScope<Change> getChangeScope(RevisionScope<ObjectId> revisionScope) throws SourceControlException {
        final ChangeScope<Change> scope = new ChangeScopeImpl();
        try {
            StopWatch sw1 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_CHANGE_SCOPE, "getFilteredEntries").start();
            List<DiffEntry> filtered = getFilteredEntries(revisionScope);
            sw1.stop();

            StopWatch sw4 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_CHANGE_SCOPE, "computeDiff for filtered entries").start();
            for (DiffEntry entry : filtered) {
                EditList editList = diffFormatter.toFileHeader(entry).toEditList();
                if (!editList.isEmpty()) {
                    computeDiffEntry(entry, editList, revisionScope, scope);
                }
            }
            sw4.stop();
        } catch (IOException e) {
            throw new SourceControlException(ErrorMessages.SCAN_REVISIONS_EXCEPTION, e);
        }
        return scope;
    }

    private List<DiffEntry> getFilteredEntries(RevisionScope<ObjectId> revisionScope) throws IOException {
        AbstractTreeIterator from = getAbstractTreeIterator(revisionScope.getFrom());
        AbstractTreeIterator to = getAbstractTreeIterator(revisionScope.getTo());

        if (!revisionScope.getTo().equals(ObjectId.zeroId())) {
            return getEntriesFromDiff(from, to);
        } else {
            return getEntriesFromStatusCall(from, to);
        }
    }

    private List<DiffEntry> getEntriesFromStatusCall(AbstractTreeIterator from, AbstractTreeIterator to) throws IOException {
        Status status = PorcelainApi.call(repository, git -> git.status().call());

        if (status.hasUncommittedChanges()) {
            Set<String> toBeCommittedChanges = new HashSet<>();
            toBeCommittedChanges.addAll(status.getModified());
            toBeCommittedChanges.addAll(status.getChanged());
            toBeCommittedChanges.addAll(status.getAdded());

            if (toBeCommittedChanges.size() == 1) {
                diffFormatter.setPathFilter(PathSuffixFilter.create(toBeCommittedChanges.iterator().next()));
            } else {
                diffFormatter.setPathFilter(OrTreeFilter.create(toBeCommittedChanges.stream().map(PathSuffixFilter::create).collect(Collectors.toSet())));
            }

            List<DiffEntry> diffEntries = diffFormatter.scan(from, to);

            return toBeCommittedChanges.parallelStream()
                    .map(toBeCommittedChange -> diffEntries.stream()
                            .filter(diffEntry -> diffEntry.getNewPath().equals(toBeCommittedChange))
                            .findFirst().orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private List<DiffEntry> getEntriesFromDiff(AbstractTreeIterator from, AbstractTreeIterator to) throws IOException {
        return diffFormatter.scan(from, to);
    }

    private void computeDiffEntry(DiffEntry entry, EditList editList, RevisionScope<ObjectId> revisionScope, ChangeScope<Change> changeScope) throws IOException {
        final String filename;
        final String content;

        final Function<RawText, String> f = t -> t.getString(0, t.size(), false);

        if (entry.getChangeType() == DiffEntry.ChangeType.DELETE) {
            content = f.apply(getRawText(revisionScope.getFrom(), entry.getOldPath()));
            filename = RawUtil.getFullClassPath(content, entry.getOldPath());
            Change change = getChangedLinesFromRemovedFile(editList, filename, content);
            changeScope.addChange(change);
        } else {
            content = f.apply(getRawText(revisionScope.getTo(), entry.getNewPath()));
            filename = RawUtil.getFullClassPath(content, entry.getNewPath());
            editList.forEach(edit -> {
                final Change change = getLinesFromChangedFile(edit, filename, content);
                changeScope.addChange(change);

            });
        }
    }

    private RawText getRawText(ObjectId objectId, String path) throws IOException {
        RawText rawText;
        if (objectId.equals(ObjectId.zeroId())) {
            rawText = getLocalFile(path);
        } else {
            rawText = getCommittedRawText(objectId, path);
        }
        return rawText;
    }

    private RawText getCommittedRawText(ObjectId revision, String filepath) throws IOException {
        try (RevWalk walker = new RevWalk(repository)) {
            RevCommit commit = walker.parseCommit(revision);
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(true);
                treeWalk.setFilter(PathFilter.create(filepath));
                if (!treeWalk.next()) {
                    return null;
                }
                ObjectId objectId = treeWalk.getObjectId(0);
                return new RawText(repository.open(objectId).getBytes());
            }
        }
    }

    private RawText getLocalFile(String filePath) throws IOException {
        String basePath = Paths.get(repository.getDirectory().getAbsolutePath(), "/..").toString();
        File file = new File(basePath, filePath);
        if (!file.exists()) {
            return null;
        }
        return new RawText(file);
    }

    private Change getChangedLinesFromRemovedFile(EditList edits, String path, String content) {
        final Set<Integer> changedLines = new HashSet<>();
        edits.forEach(edit -> changedLines.addAll(RawUtil.getIntRange(edit.getBeginA() + 1, edit.getEndA() + 1)));
        return createChange(path, content, changedLines, ChangeType.DELETE);
    }

    private Change createChange(String path, String content, Set<Integer> changedLines, ChangeType changeType) {
        return new SCMChangeBuilder().with(scm -> {
            scm.path = path;
            scm.changedLines = changedLines;
            scm.changeType = changeType;
            scm.fileContent = content;
            scm.repository = repository.getWorkTree().getAbsolutePath();
        }).build();
    }

    private ChangeType getType(Edit edit) {
        if (edit.getType() == Edit.Type.DELETE)
            return ChangeType.DELETE;
        if (edit.getType() == Edit.Type.INSERT)
            return ChangeType.ADD;
        if (edit.getType() == Edit.Type.REPLACE)
            return ChangeType.MODIFY;
        return ChangeType.EMPTY;
    }

    private Change getLinesFromChangedFile(final Edit edit, final String path, String content) {
        Set<Integer> changedLines = new HashSet<>();
        if (edit.getType() == Edit.Type.DELETE) {
            changedLines.addAll(RawUtil.getIntRange(edit.getBeginB(), edit.getEndB() + 2));
        } else {
            changedLines.addAll(RawUtil.getIntRange(edit.getBeginB() + 1, edit.getEndB() + 1));
        }

        return createChange(path, content, changedLines, getType(edit));
    }

    private AbstractTreeIterator getAbstractTreeIterator(ObjectId objectId) throws IOException {
        if (objectId.equals(ObjectId.zeroId())) {
            return new FileTreeIterator(repository);
        }

        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(objectId);
            try (ObjectReader reader = repository.newObjectReader()) {
                return new CanonicalTreeParser(null, reader, commit.getTree());
            }
        }
    }

    public static class DiffFormatterBuilder {
        Repository repository;
        Set<TreeFilter> filters;

        OutputStream outputStream;
        RawTextComparator diffComparator;
        boolean doDetectRenames;
        int context;
        DiffAlgorithm diffAlgo;


        DiffFormatterBuilder() {
            this.outputStream = DisabledOutputStream.INSTANCE;
            this.diffComparator = RawTextComparator.WS_IGNORE_ALL;
            this.doDetectRenames = true;
            this.context = 0;
            this.diffAlgo = new HistogramDiff();

            filters = new HashSet<>();
        }

        DiffFormatterBuilder with(Consumer<DiffFormatterBuilder> function) {
            function.accept(this);
            return this;
        }

        DiffFormatter build() {
            final DiffFormatter diffFormatter = new DiffFormatter(this.outputStream);
            diffFormatter.setRepository(this.repository);

            diffFormatter.setDiffComparator(this.diffComparator);
            diffFormatter.setDetectRenames(this.doDetectRenames);
            diffFormatter.setContext(this.context);
            diffFormatter.setDiffAlgorithm(this.diffAlgo);

            if (this.filters.size() == 1)
                diffFormatter.setPathFilter(this.filters.iterator().next());
            else if (this.filters.size() > 1)
                diffFormatter.setPathFilter(OrTreeFilter.create(this.filters));

            return diffFormatter;
        }
    }
}
