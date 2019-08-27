package com.sandboni.core.scm.scope;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChangeScopeAnalyzerTest {

    private ChangeScopeImpl changeScope;

    private final String class1Name = "com/sandboni/core/sandboni/A";
    private final String pomFile = "com/sandboni/core/sandboni/pom.xml";
    private final String pomFileUpperCase = "com/sandboni/core/SANDBONI/POM.XML";


    @Before
    public void beforeTest(){
        this.changeScope = new ChangeScopeImpl();
    }

    @Test
    public void testBasicAnalyzer(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        changeScope.addChange(new SCMChange(pomFile, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertTrue(ChangeScopeAnalyzer.detectConfigurationFile(changeScope, "pom.xml"));
    }

    @Test
    public void testBasicAnalyzerInsensitive(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());

        changeScope.addChange(new SCMChange(pomFileUpperCase, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertTrue(ChangeScopeAnalyzer.detectConfigurationFile(changeScope, "pom.xml"));
    }

    @Test
    public void testAnalyzerMultipleCnfgTypes(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        changeScope.addChange(new SCMChange(pomFileUpperCase, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertTrue(ChangeScopeAnalyzer.detectConfigurationFile(changeScope, "pom.xml", "gradle.build"));
    }

    @Test
    public void testAnalyzerWhenScopeIsEmpty(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        Assert.assertFalse(ChangeScopeAnalyzer.detectConfigurationFile(changeScope, "pom.xml", "build.gradle"));
    }

    @Test
    public void testAnalyzerWhenNoCnfFilesInScope(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(ChangeScopeAnalyzer.detectConfigurationFile(changeScope, "pom.xml", "build.gradle"));
    }

    @Test
    public void testAnalyzerWhenNoCnfFileAndRegularFiles(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(pomFile, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertTrue(ChangeScopeAnalyzer.detectConfigurationFile(changeScope, "pom.xml", "build.gradle"));
    }
}
