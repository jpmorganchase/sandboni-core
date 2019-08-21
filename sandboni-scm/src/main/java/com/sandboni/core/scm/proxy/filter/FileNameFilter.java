package com.sandboni.core.scm.proxy.filter;

import com.sandboni.core.scm.proxy.SourceControlFilter;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileNameFilter implements SourceControlFilter {

    private final Set<TreeFilter> fileNamesToInclude;

    public FileNameFilter(FileNames... fileNames){
        fileNamesToInclude = new HashSet<>();
        if (fileNames != null){
            Arrays.stream(fileNames).forEach(f -> fileNamesToInclude.add(PathSuffixFilter.create(f.fileName())));
        }
    }

    @Override
    public Set<TreeFilter> getCriteria() {
        return fileNamesToInclude;
    }
}