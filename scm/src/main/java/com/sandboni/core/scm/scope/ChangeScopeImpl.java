package com.sandboni.core.scm.scope;

import com.sandboni.core.scm.proxy.filter.FileExtensions;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class ChangeScopeImpl implements ChangeScope<Change> {

    private Map<String, List<Change>> changes;

    public ChangeScopeImpl() {
        //we want the 'changes' to be either concurrent and case insensitive when looking for records
        this.changes = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void addChange(Change change) {
        List<Change> changesList = changes.get(change.getFilename());
        if (Objects.nonNull(changesList)) {
            Change localChange = changesList.stream()
                    .filter(c -> c.getType() == change.getType())
                    .findFirst().orElse(null);
            if (Objects.isNull(localChange)) {
                changesList.add(change);
            } else {
                localChange.addChangedLines(change.getLinesChanged());
            }
        } else {
            changesList = new LinkedList<>();
            changesList.add(change);
            changes.put(change.getFilename(), changesList);
        }
    }

    @Override
    public List<Change> getChanges(String className) {
        return changes.get(className);
    }

    @Override
    public List<Change> getChangesForFilesEndWith(String fileName) {
        return changes.entrySet().stream()
                .filter(e-> e.getKey().toLowerCase().endsWith(fileName))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public boolean contains(String className) {
        return changes.containsKey(className);
    }

    @Override
    public Set<Integer> getAllLinesChanged(String className) {
        if (changes.containsKey(className)) {
            return changes.get(className).stream()
                    .map(Change::getLinesChanged)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getAllAffectedClasses() {
        return changes.keySet();
    }

    @Override
    public void remove(String className) {
        changes.remove(className);
    }

    @Override
    public void remove(FileExtensions... fe) {
        Arrays.stream(fe).forEach(f->
                changes.entrySet().removeIf(e-> e.getKey().endsWith(f.extension())));
    }

    @Override
    public void include(FileExtensions... fe) {
        changes.entrySet().removeIf(e -> Arrays.stream(fe).noneMatch(f -> e.getKey().endsWith(f.extension())));
    }

    @Override
    public boolean isEmpty() {
        return changes.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        final String sep = ":";
        changes.forEach((k, v) ->
                v.forEach(c ->
                        builder.append(k).append(sep)
                                .append(c.getType()).append(sep)
                                .append(c.getLinesChanged()).append("\n")));
        return builder.toString();
    }
}
