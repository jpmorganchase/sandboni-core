package com.sandboni.core.scm.scope.analysis;

import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeType;
import com.sandboni.core.scm.scope.SCMChangeBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangeScannersTest {

    private ChangeScanners falseScan;
    private ChangeScanners trueScan;


    @Before
    public void init(){
        falseScan = new ChangeScanners(f-> false, f-> true);
        trueScan = new ChangeScanners(f-> true, f-> true);
    }

    @Test
    public void testFalseScans(){
        Assert.assertFalse(falseScan.scan(createChange()));
    }

    @Test
    public void testTrueScans(){
        Assert.assertTrue(trueScan.scan(createChange()));
    }

    private Change createChange(){
        return new SCMChangeBuilder().with(scm -> {
            scm.path = "a/b/c";
            scm.changedLines = Stream.of(1,2).collect(Collectors.toSet());
            scm.changeType = ChangeType.MODIFY;
            scm.fileContent = "";
        }).build();
    }
}
