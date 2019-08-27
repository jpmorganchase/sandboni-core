package com.sandboni.core.engine.result;

import com.sandboni.core.scm.GitInterface;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.scope.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeDetectorResultMock implements GitInterface {

    @Override
    public ChangeScope getChanges(String fromRev, String toRev){
        ChangeScope<Change> changeScope = new ChangeScopeImpl();
        if ("0".equals(fromRev)) {
            //empty
            return changeScope;
        } else{
            Set<Integer> s = new HashSet<>();
            s.add(1);
            s.add(6);

            if ("1".equals(fromRev)) {
                //only cnfg
                changeScope = new ChangeScopeImpl();

                changeScope.addChange(new SCMChange("pom.xml", s, ChangeType.ADD));
            } else if ("2".equals(fromRev)) {
                //only java
                changeScope = new ChangeScopeImpl();
                changeScope.addChange(new SCMChange("change.java", s, ChangeType.ADD));

            } else if ("3".equals(fromRev)) {
                //both
                changeScope = new ChangeScopeImpl();
                changeScope.addChange(new SCMChange("pom.xml", s, ChangeType.ADD));
                changeScope.addChange(new SCMChange("change.java", s, ChangeType.MODIFY));
            }
        }
        return changeScope;
    }

    @Override
    public Set<RevInfo> getJiraSet(String s, List<Integer> list) {
        return null;
    }
}
