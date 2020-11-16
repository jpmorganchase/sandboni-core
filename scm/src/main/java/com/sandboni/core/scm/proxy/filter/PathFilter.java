package com.sandboni.core.scm.proxy.filter;

import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.proxy.SourceControlFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PathFilter implements SourceControlFilter {

    private Set<TreeFilter> pathsToScan;

    public PathFilter(String... locationsToScan) throws SourceControlException {

        if (locationsToScan == null)
            throw new SourceControlException("Please provide at least one path you want git to inspect");

        this.pathsToScan = new HashSet<>(locationsToScan.length, 0.6f);
        Arrays.stream(locationsToScan).forEach(l->pathsToScan.add(org.eclipse.jgit.treewalk.filter.PathFilter.create(l)));
    }

    @Override
    public Set<TreeFilter> getCriteria() {
        return pathsToScan;
    }
}
