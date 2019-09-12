package com.sandboni.core.scm.scope;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChangeScopeAnalyzer {
    private ChangeScopeAnalyzer() {}

    /**
     * detect configuration files in the change scope
     * @return boolean if any provided confg file exists in change scope
     */
    public static <T> boolean detectConfigurationFile(ChangeScope<T> changeScope, String ... files) {
        Set<String> configurations = new HashSet<>();
        Arrays.stream(files).forEach(f-> configurations.add(f.toLowerCase()));
        //in case ",* -classname " -> only ends with this className then:
        return changeScope.getAllAffectedClasses()
                .stream()
                .anyMatch(entry -> configurations.stream().anyMatch(entry.toLowerCase()::endsWith));
    }
}
