package com.sandboni.core.scm.proxy.filter;

import com.sandboni.core.scm.proxy.SourceControlFilter;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileExtensionFilter implements SourceControlFilter {

    private final Set<TreeFilter> fileExtensionsToInclude;

    public FileExtensionFilter(FileExtensions... fileExtensions){
        fileExtensionsToInclude = new HashSet<>();
        if (fileExtensions != null){
            Arrays.stream(fileExtensions).forEach(f -> fileExtensionsToInclude.add(PathSuffixFilter.create(f.extension())));
        }
    }

    @Override
    public Set<TreeFilter> getCriteria() {
        return fileExtensionsToInclude;
    }
}
