package com.sandboni.core.scm.resolvers;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.util.*;

import static org.eclipse.jgit.revwalk.RevSort.COMMIT_TIME_DESC;

public class TreeWalkResolver {
    private final Repository repository;
    private final String commitId;

    private List<Map.Entry<String, String>> commitTree;

    public TreeWalkResolver(Repository repository, String commitId) {
        this.repository = repository;
        this.commitId = commitId;
    }

    public String getFullPath(String packageName) throws IOException {
        List<Map.Entry<String, String>> tree = getCommitTree();
        String reversed = new StringBuilder(packageName).reverse().toString();
        int index = Collections.binarySearch(tree, new AbstractMap.SimpleEntry<>(reversed, packageName),
                (o1, o2) -> o1.getKey().startsWith(o2.getKey()) ? 0 : o1.getKey().compareTo(o2.getKey()));
        return index >= 0 ? tree.get(index).getValue() : null;
    }

    private List<Map.Entry<String, String>> getCommitTree() throws IOException {
        if (commitTree == null) {
            commitTree = fullTreeWalk();
        }
        return commitTree;
    }

    private List<Map.Entry<String, String>> fullTreeWalk() throws IOException {
        List<Map.Entry<String, String>> map = new ArrayList<>();
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            RevCommit commit = getRevCommit();
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                String reversed = new StringBuilder(treeWalk.getPathString()).reverse().toString();
                Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(reversed, treeWalk.getPathString());
                map.add(entry);
            }
        }
        map.sort(Comparator.comparing(Map.Entry::getKey));
        return map;
    }

    private RevCommit getRevCommit() throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            walk.sort(COMMIT_TIME_DESC);
            return walk.parseCommit(repository.resolve(commitId));
        }
    }
}
