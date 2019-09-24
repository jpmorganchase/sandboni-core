package com.sandboni.core.scm.scope;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SCMChangeBuilderTest {

    private String pom = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n" +
            "<modelVersion>4.0.0</modelVersion>\r\n<groupId>com.github.jpmorganchase.sandboni</groupId>\r\n<artifactId>sandboni-core</artifactId>\r\n<version>0.0.1</version>\r\n</project>";


    @Test
    public void testPXMLPath(){
        Change change = createChange("src/test/resources/parentPOM.xml", Stream.of(2).collect(Collectors.toSet()), ChangeType.MODIFY, pom) ;
        Assert.assertTrue(change instanceof SCMChangeInBuildFile);
        Assert.assertEquals("src/test/resources/parentPOM.xml", change.getFilename());
        Assert.assertEquals(1, change.getLinesChanged().size());
        Assert.assertEquals(ChangeType.MODIFY, change.getType());
        Assert.assertTrue(change.getModel().isPresent());
        Assert.assertTrue(change.getFileContent().isPresent());
        Assert.assertEquals(pom, change.getFileContent().get());
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
