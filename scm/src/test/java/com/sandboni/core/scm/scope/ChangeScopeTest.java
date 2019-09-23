package com.sandboni.core.scm.scope;

import com.sandboni.core.scm.proxy.filter.FileExtensions;
import com.sandboni.core.scm.scope.analysis.ChangeScopeAnalyzer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChangeScopeTest {

    private ChangeScopeImpl changeScope;

    private final String class1Name = "com/sandboni/core/sandboni/A";
    private final String class2Name = "com/sandboni/core/sandboni/B";
    private final String xmlFile = "com/sandboni/core/sandboni/B.xml";
    private final String ymlFile = "com/sandboni/core/sandboni/B.yml";
    private final String gradleFile = "com/sandboni/core/sandboni/build.gradle";
    private final String featureFile = "resources/features/signin.feature";


    @Before
    public void beforeTest(){
        this.changeScope = new ChangeScopeImpl();
    }

    @Test
    public void testAddingOneChangeToOneClass(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());

        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertEquals(1, changeScope.getAllAffectedClasses().size());
        Assert.assertEquals(1, changeScope.getChanges(class1Name).size());
        Assert.assertEquals(100, changeScope.getAllLinesChanged(class1Name).size());
    }

    @Test
    public void testAddingChangesToTwoClasses(){
        Assert.assertTrue(changeScope.getAllAffectedClasses().isEmpty());

        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(class2Name, IntStream.range(1, 101).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertEquals(2, changeScope.getAllAffectedClasses().size());
        Assert.assertEquals(1, changeScope.getChanges(class1Name).size());
        Assert.assertEquals(100, changeScope.getAllLinesChanged(class1Name).size());
        Assert.assertEquals(100, changeScope.getAllLinesChanged(class2Name).size());
        Assert.assertEquals(1, changeScope.getChanges(class2Name).size());
    }

    @Test
    public void testAddingChangesWithSameTypesToOneClass(){
        Assert.assertTrue(changeScope.isEmpty());

        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(11, 21).boxed().collect(Collectors.toSet()), ChangeType.ADD));

        Assert.assertEquals(1, changeScope.getAllAffectedClasses().size());
        Assert.assertEquals(1, changeScope.getChanges(class1Name).size());
        Assert.assertEquals(20, changeScope.getChanges(class1Name).get(0).getLinesChanged().size());
        Assert.assertEquals(20, changeScope.getAllLinesChanged(class1Name).size());
    }

    @Test
    public void testAddingChangesWithDifferentTypesToOneClass(){
        Assert.assertTrue(changeScope.isEmpty());

        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(11, 21).boxed().collect(Collectors.toSet()), ChangeType.DELETE));

        Assert.assertEquals(1, changeScope.getAllAffectedClasses().size());
        Assert.assertEquals(2, changeScope.getChanges(class1Name).size());
        Assert.assertEquals(10, changeScope.getChanges(class1Name).get(0).getLinesChanged().size());
        Assert.assertEquals(10, changeScope.getChanges(class1Name).get(1).getLinesChanged().size());
        Assert.assertEquals(20, changeScope.getAllLinesChanged(class1Name).size());
    }

    @Test
    public void testAddingChangesWithDifferentTypesToTwoClasses(){
        Assert.assertTrue(changeScope.isEmpty());

        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(class2Name, IntStream.range(11, 21).boxed().collect(Collectors.toSet()), ChangeType.DELETE));
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(11, 21).boxed().collect(Collectors.toSet()), ChangeType.DELETE));
        changeScope.addChange(new SCMChange(class2Name, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));

        Assert.assertEquals(2, changeScope.getAllAffectedClasses().size());
        Assert.assertEquals(2, changeScope.getChanges(class1Name).size());
        Assert.assertEquals(2, changeScope.getChanges(class2Name).size());
        Assert.assertEquals(20, changeScope.getAllLinesChanged(class1Name).size());
        Assert.assertEquals(20, changeScope.getAllLinesChanged(class2Name).size());
    }

    @Test
    public void testGetAllAffectedLines(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(11, 21).boxed().collect(Collectors.toSet()), ChangeType.ADD));

        Assert.assertNotNull(changeScope.getAllLinesChanged(class1Name));
        Assert.assertTrue(changeScope.getAllLinesChanged(class2Name).isEmpty());
    }

    @Test
    public void testToString(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(11, 21).boxed().collect(Collectors.toSet()), ChangeType.ADD));

        Assert.assertEquals("com/sandboni/core/sandboni/A:ADD:[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]\n", changeScope.toString());
    }

    @Test
    public void tesIsEmptyInitial(){
        Assert.assertTrue(changeScope.isEmpty());
    }

    @Test
    public void testRemoveByClassName(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(changeScope.isEmpty());
        changeScope.remove(class1Name);
        Assert.assertTrue(changeScope.isEmpty());
    }

    @Test
    public void testRemoveByOneExt(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(xmlFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(changeScope.isEmpty());
        changeScope.remove(FileExtensions.XML);
        Assert.assertTrue(changeScope.isEmpty());
    }

    @Test
    public void testRemoveByTwoExt(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(xmlFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(ymlFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(changeScope.isEmpty());
        changeScope.remove(FileExtensions.XML, FileExtensions.YML);
        Assert.assertTrue(changeScope.isEmpty());
    }

    @Test
    public void testRemoveByTwoExtStillContainsJava(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(xmlFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(ymlFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(class1Name, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(changeScope.isEmpty());
        changeScope.remove(FileExtensions.XML, FileExtensions.YML);
        Assert.assertFalse(changeScope.isEmpty());
    }

    @Test
    public void testIncludeByOneExt(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(xmlFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(changeScope.isEmpty());
        changeScope.include(FileExtensions.XML);
        Assert.assertEquals(1, changeScope.getAllAffectedClasses().size());
    }

    @Test
    public void testIncludeEmptyScope(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.include(FileExtensions.XML);
        Assert.assertEquals(0, changeScope.getAllAffectedClasses().size());
    }

    @Test
    public void testIncludeByTwoExts(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(xmlFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(ymlFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        changeScope.addChange(new SCMChange(featureFile, IntStream.range(3, 4).boxed().collect(Collectors.toSet()), ChangeType.MODIFY));
        String javaFile = "com/sandboni/core/sandboni/MyClass.java";
        changeScope.addChange(new SCMChange(javaFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(changeScope.isEmpty());
        changeScope.include(FileExtensions.JAVA, FileExtensions.FEATURE);
        Assert.assertEquals(2, changeScope.getAllAffectedClasses().size());
    }


    @Test
    public void testEndsWith(){
        Assert.assertTrue(changeScope.isEmpty());
        String pomFilePath = "com/sandboni/core/sandboni/pom.xml";
        changeScope.addChange(new SCMChange(pomFilePath, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.MODIFY));
        Assert.assertFalse(changeScope.isEmpty());
        Assert.assertTrue(changeScope.contains(pomFilePath));
        String pomFile = "pom.xml";
        Assert.assertFalse(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, pomFile));
    }

    @Test
    public void testEndsWithIncasSensitive(){
        Assert.assertTrue(changeScope.isEmpty());
        String pomFilePath = "com/sandboni/core/sandboni/POM.XML";
        changeScope.addChange(new SCMChange(pomFilePath, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.MODIFY));
        Assert.assertFalse(changeScope.isEmpty());
        Assert.assertTrue(changeScope.contains(pomFilePath));
        String pomFile = "pom.xml";
        Assert.assertFalse(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, pomFile));
    }

    @Test
    public void testEndsWithIncasSensitive2(){
        Assert.assertTrue(changeScope.isEmpty());
        String pomFilePath = "com/sandboni/core/sandboni/pom.xml";
        changeScope.addChange(new SCMChange(pomFilePath, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.MODIFY));
        Assert.assertFalse(changeScope.isEmpty());
        Assert.assertTrue(changeScope.contains(pomFilePath));
        String pomFile = "POM.XML";
        Assert.assertTrue(ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, pomFile));
    }



    @Test
    public void testGradleIsInserted(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(gradleFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(changeScope.isEmpty());
        Assert.assertTrue(changeScope.contains(gradleFile));
    }

    @Test
    public void testIgnoreCase(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(gradleFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(changeScope.isEmpty());
        Assert.assertTrue(changeScope.contains(gradleFile.toUpperCase()));
    }

    @Test
    public void testGradleIsRemoved(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(gradleFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertFalse(changeScope.isEmpty());
        Assert.assertTrue(changeScope.contains(gradleFile));
        changeScope.remove(gradleFile);
        Assert.assertTrue(changeScope.isEmpty());
    }

    @Test
    public void testContains(){
        Assert.assertTrue(changeScope.isEmpty());
        changeScope.addChange(new SCMChange(xmlFile, IntStream.range(1, 11).boxed().collect(Collectors.toSet()), ChangeType.ADD));
        Assert.assertTrue(changeScope.contains(xmlFile));

    }











}
