package com.sandboni.core.scm.scope.analysis;

import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeType;
import com.sandboni.core.scm.scope.SCMChangeBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    @Test
    public void testDeletedPropertiesLines() {
        Set<Integer> changedLines = Stream.of(0,1).collect(Collectors.toSet());
        Change change = new SCMChangeBuilder().with(scm -> {
            scm.path = "a.yml";
            scm.changedLines = changedLines;
            scm.changeType = ChangeType.DELETE;
            scm.fileContent = "";
        }).build();

        assertNotNull(change);
        assertEquals("a.yml", change.getFilename());
        assertEquals(ChangeType.DELETE, change.getType());
        assertEquals(changedLines, change.getLinesChanged());
    }

    @Test
    public void testDeletedBuildLines() {
        Set<Integer> changedLines = Stream.of(0,1).collect(Collectors.toSet());
        Change change = new SCMChangeBuilder().with(scm -> {
            scm.path = "pom.xml";
            scm.changedLines = changedLines;
            scm.changeType = ChangeType.DELETE;
            scm.fileContent = "";
        }).build();

        assertNotNull(change);
        assertEquals("pom.xml", change.getFilename());
        assertEquals(ChangeType.DELETE, change.getType());
        assertEquals(changedLines, change.getLinesChanged());
    }
}
