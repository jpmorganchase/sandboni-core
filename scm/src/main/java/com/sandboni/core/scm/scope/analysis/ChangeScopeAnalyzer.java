package com.sandboni.core.scm.scope.analysis;

import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeScopeAnalyzer {

    private ChangeScopeAnalyzer() { }

    /**
     * detect configuration files in the change scope
     *
     * @return boolean true only if
     */
    public static boolean analyzeConfigurationFiles(ChangeScope<Change> changeScope, String... files) {

        List<Change> changes = new ArrayList<>();
        Arrays.stream(files).forEach(f-> changes.addAll(changeScope.getChangesForFilesEndWith(f)));

        if (changes.isEmpty()) //no configuration files
            return true;

        List<Change> filteredChanges =  changes.stream()
                .filter(c -> c.getFileContent().isPresent() && c.getModel().isPresent())
                .collect(Collectors.toList());

        //if changes left empty after filtering, it means that we had a problem in creating model/content(exception in Maven or it's a Gradle build)
        //in both cases we dont take extra risk and return false
        if (filteredChanges.isEmpty())
            return false;

        return changes.stream().allMatch(ChangeScanners.ALL::scan);
    }
}