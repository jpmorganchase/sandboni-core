package com.sandboni.core.scm.proxy;

import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.util.Set;

public interface SourceControlFilter {

    Set<TreeFilter> getCriteria();
}
