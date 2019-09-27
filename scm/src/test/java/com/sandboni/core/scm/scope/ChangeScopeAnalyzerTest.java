package com.sandboni.core.scm.scope;

import com.sandboni.core.scm.scope.analysis.ChangeScopeAnalyzer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChangeScopeAnalyzerTest {

    private ChangeScopeImpl changeScope;

    private final String class1Name = "com/sandboni/core/sandboni/A";
    private final String pomFile = "com/sandboni/core/sandboni/pom.xml";
    private final String pomFileUpperCase = "com/sandboni/core/SANDBONI/POM.XML";
    private final String gradleBuild = "com/sandboni/core/SANDBONI/build.gradle";

    private String pom = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n" +
            "<modelVersion>4.0.0</modelVersion>\r\n<groupId>com.github.jpmorganchase.sandboni</groupId>\r\n<artifactId>sandboni-core</artifactId>\r\n<version>0.0.1</version>\r\n</project>";



    @Before
    public void beforeTest(){
        this.changeScope = new ChangeScopeImpl();
    }

    @Test
    public void testBasicAnalyzer(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        changeScope.addChange(new SCMChange(pomFile, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, "pom.xml"));
    }

    @Test
    public void testBasicAnalyzerInsensitive(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());

        changeScope.addChange(new SCMChange(pomFileUpperCase, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, "pom.xml"));
    }

    @Test
    public void testAnalyzerMultipleCnfgTypes(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        changeScope.addChange(new SCMChange(pomFileUpperCase, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, "pom.xml", "gradle.build"));
    }

    @Test
    public void testAnalyzerWhenScopeIsEmpty(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        Assert.assertTrue(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, "pom.xml", "build.gradle"));
    }

    @Test
    public void testAnalyzerWhenNoCnfFilesInScope(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertTrue(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, "pom.xml", "build.gradle"));
    }

    @Test
    public void testAnalyzerWhenNoCnfFileAndRegularFiles(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(pomFile, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, "pom.xml", "build.gradle"));
    }


    @Test
    public void testWhenGradleBuild(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChangeInBuildFile(gradleBuild, IntStream.range(1, 2).boxed().collect(Collectors.toSet()), ChangeType.MODIFY, "{file-content}", null));
        Assert.assertFalse(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope,  "build.gradle"));
    }


    @Test
    public void testPOMWithValidSCMConfigurationFile(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        Change change = createChange("src/test/resources/parentPOM.xml", Stream.of(5).collect(Collectors.toSet()), ChangeType.MODIFY, pom);
        changeScope.addChange(change);
        Assert.assertTrue(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope,  "pom/xml"));
    }

    @Test
    public void testPOMWithNotValidSCMConfigurationFile(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        Change change = createChange("src/test/resources/parentPOM.xml", Stream.of(5).collect(Collectors.toSet()), ChangeType.MODIFY, null);
        changeScope.addChange(change);
        Assert.assertFalse(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope,  "pom.xml"));
    }

    @Test
    public void testGradleAndMavenConfigurationFiles(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());
        Change change = createChange("src/test/resources/parentPOM.xml", Stream.of(5).collect(Collectors.toSet()), ChangeType.MODIFY, null);
        changeScope.addChange(change);
        Assert.assertFalse(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope,  "pom.xml"));
    }



    private Change createChange(String path, Set<Integer> lines, ChangeType type, String pom){
        return new SCMChangeBuilder().with(scm -> {
            scm.path = path;
            scm.changedLines = lines;
            scm.changeType = type;
            scm.fileContent = pom;
        }).build();
    }





}
