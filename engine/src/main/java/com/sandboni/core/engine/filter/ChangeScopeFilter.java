package com.sandboni.core.engine.filter;

import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;

import java.io.File;
import java.util.List;
import java.util.Set;

import static com.sandboni.core.scm.proxy.filter.FileExtensions.*;
import static java.util.stream.Collectors.*;

public class ChangeScopeFilter implements ScopeFilter<ChangeScope<Change>, Set<File>> {

    @Override
    public boolean isInScope(ChangeScope<Change> changeScope, Set<File> mainSourceDirs) {
        // cartesian product of mainSourceDirs and changeScope
        List<String> filePaths = mainSourceDirs.stream()
                .map(dir -> changeScope.getAllAffectedClasses().stream()
                        .map(className -> className.replace(JAVA.extension(), CLASS.extension()))
                        .map(name -> dir + File.separator + name)
                        .collect(toList()))
                .flatMap(List::stream)
                .collect(toList());

        // we just need to find one changed class in the current module source code
        return filePaths.stream()
                .map(File::new)
                .anyMatch(File::exists);
    }
}
