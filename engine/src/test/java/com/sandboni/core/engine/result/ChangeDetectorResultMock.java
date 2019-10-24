package com.sandboni.core.engine.result;

import com.sandboni.core.scm.GitInterface;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.scope.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangeDetectorResultMock implements GitInterface {

    private Change createChange(){
        return new SCMChangeBuilder().with(scm -> {
            scm.path = "pom.xml";
            scm.changedLines = Stream.of(1,2).collect(Collectors.toSet());
            scm.changeType = ChangeType.MODIFY;
            scm.fileContent = "";
        }).build();
    }

    private Change createParentPOMChange(){
        return new SCMChangeBuilder().with(scm -> {
            String rootPom = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n" +
                    "<modelVersion>4.0.0</modelVersion>\r\n<groupId>com.github.jpmorganchase.sandboni</groupId>\r\n<artifactId>sandboni-core</artifactId>\r\n<version>0.0.1</version>\r\n</project>";

            scm.path = "../scm/src/test/resources/parentPOM.xml";
            scm.changedLines = Stream.of(5).collect(Collectors.toSet());
            scm.changeType = ChangeType.MODIFY;
            scm.fileContent = rootPom;
        }).build();
    }

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

            changeScope = new ChangeScopeImpl();
            if ("1".equals(fromRev)) {
                //only cnfg
                changeScope.addChange(createChange());
            } else if ("2".equals(fromRev)) {
                //only java
                changeScope.addChange(new SCMChange("change.java", s, ChangeType.ADD));

            } else if ("3".equals(fromRev)) {
                //both
                changeScope.addChange(createChange());
                changeScope.addChange(new SCMChange("change.java", s, ChangeType.MODIFY));
            } else if ("4".equals(fromRev)) {
                //both
                changeScope.addChange(createParentPOMChange());
                changeScope.addChange(new SCMChange("change.java", s, ChangeType.MODIFY));
            } else if ("5".equals(fromRev)) {
                // only non supported files
                changeScope.addChange(new SCMChange("script.sql", s, ChangeType.MODIFY));
            }
            else if ("6".equals(fromRev)) {
                // config, non supported files
                changeScope.addChange(createParentPOMChange());
                changeScope.addChange(new SCMChange("script.sql", s, ChangeType.MODIFY));
            } else if ("7".equals(fromRev)) {
                // Java, config, non supported files
                changeScope.addChange(createParentPOMChange());
                changeScope.addChange(new SCMChange("change.java", s, ChangeType.MODIFY));
                changeScope.addChange(new SCMChange("script.sql", s, ChangeType.MODIFY));
            } else if ("8".equals(fromRev)) {
                // Java, non supported files
                changeScope.addChange(new SCMChange("change.java", s, ChangeType.MODIFY));
                changeScope.addChange(new SCMChange("script.sql", s, ChangeType.MODIFY));
            }
        }
        return changeScope;
    }

    @Override
    public Set<RevInfo> getJiraSet(String s, List<Integer> list) {
        return null;
    }
}
