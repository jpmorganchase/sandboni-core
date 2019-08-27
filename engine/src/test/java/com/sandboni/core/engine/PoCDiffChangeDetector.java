package com.sandboni.core.engine;

import com.sandboni.core.scm.GitInterface;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.scope.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PoCDiffChangeDetector implements GitInterface {

    public ChangeScope<Change> getChanges(String fromCommitId, String toCommitId) {
        ChangeScope<Change> changeScope = new ChangeScopeImpl();

        //MOCKING UP: for test scenarios
        changeScope.addChange(new SCMChange("com/sandboni/core/scenario/Callee.java", IntStream.range(1, 100).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange("com/sandboni/core/scenario/DoOtherStuff.java", IntStream.range(1, 100).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange("com/sandboni/core/scenario/JavaxController.java", IntStream.range(1, 100).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange("com/sandboni/core/scenario/SpringController.java", IntStream.range(1, 100).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange("com/sandboni/core/scenario/CallerBase.java", IntStream.range(1, 100).boxed().collect(Collectors.toSet()), ChangeType.ADD));

        return changeScope;
    }

    @Override
    public Set<RevInfo> getJiraSet(String s, List<Integer> list) {
        return null;
    }
}