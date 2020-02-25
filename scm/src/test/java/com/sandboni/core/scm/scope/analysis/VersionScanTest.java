package com.sandboni.core.scm.scope.analysis;

import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeType;
import com.sandboni.core.scm.scope.SCMChangeBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VersionScanTest {

    private String rootPom = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n" +
            "<modelVersion>4.0.0</modelVersion>\r\n<groupId>com.github.jpmorganchase.sandboni</groupId>\r\n<artifactId>sandboni-core</artifactId>\r\n<version>0.0.1</version>\r\n</project>";

    private String childPom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n" +
            "    <parent>\r\n" +
            "        <artifactId>sandboni-core</artifactId>\r\n" +
            "        <groupId>com.github.jpmorganchase.sandboni</groupId>\r\n" +
            "        <version>0.0.1</version>\r\n" +
            "        <relativePath>../pom.xml</relativePath>\r\n" +
            "    </parent>\r\n" +
            "    <modelVersion>4.0.0</modelVersion>\r\n" +
            "    <artifactId>sandboni-test-scm</artifactId>\r\n" +
            "    <name>Test SCM</name>\r\n" +
            "</project>";

    private String unixChildPom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
            "\n" +
            "  <modelVersion>4.0.0</modelVersion>\n" +
            "  <groupId>com.jpmchase.awm.doctracktool</groupId>\n" +
            "  <artifactId>dtt</artifactId>\n" +
            "  <version>0.0.93-SNAPSHOT</version>\n" +
            "  <packaging>pom</packaging>\n" +
            "  <name>dtt</name>\n" +
            "\n" +
            "    <parent>\n" +
            "        <artifactId>te-core</artifactId>\n" +
            "        <groupId>com.jpmc.awm.te</groupId>\n" +
            "        <version>1.0.3</version>\n" +
            "    </parent>\n" +
            "\n" +
            "</project>\n";

    private String emptyFileContent = "";

    @Before
    public void init(){
    }

    @Test
    public void testModifyFirstLine(){
        VersionScanner vs = new VersionScanner();
        Assert.assertFalse(vs.scan(createParentPOMChange(Collections.singleton(1), ChangeType.MODIFY)));
    }

    @Test
    public void testModifyVersionLine(){
        VersionScanner vs = new VersionScanner();
        Assert.assertTrue(vs.scan(createParentPOMChange(Collections.singleton(5), ChangeType.MODIFY)));
    }

    @Test
    public void testEmptyFileContent(){
        VersionScanner vs = new VersionScanner();
        Assert.assertFalse(vs.scan(createPOMChange(Collections.singleton(5), ChangeType.MODIFY, "some/path", emptyFileContent)));
    }

    @Test
    public void testModifyVersionLineWithUnixLineSeparator() throws IOException {
        VersionScanner vs = new VersionScanner();
        Assert.assertTrue(vs.scan(createChildPOMChangeUnix(Collections.singleton(14), ChangeType.MODIFY)));
    }

    @Test
    public void testAddLine(){
        VersionScanner vs = new VersionScanner();
        Assert.assertFalse(vs.scan(createParentPOMChange(Collections.singleton(5), ChangeType.ADD)));
    }

    @Test
    public void testMultipleNonVersionLines(){
        VersionScanner vs = new VersionScanner();
        Assert.assertFalse(vs.scan(createParentPOMChange(Stream.of(2,5).collect(Collectors.toSet()), ChangeType.MODIFY)));
    }

    @Test
    public void testChildVersionLines(){
        VersionScanner vs = new VersionScanner();
        Assert.assertTrue(vs.scan(createChildPOMChange((Stream.of(6).collect(Collectors.toSet())), ChangeType.MODIFY)));
    }

    private Change createParentPOMChange(Set<Integer> lines, ChangeType type){
        return createPOMChange(lines, type, "src/test/resources/parentPOM.xml", rootPom);
    }

    private Change createChildPOMChangeUnix(Set<Integer> lines, ChangeType type) throws IOException {
            return createPOMChange(lines, type, "src/test/resources/childUnixPOM.xml", unixChildPom);
    }

    private Change createPOMChange(Set<Integer> lines, ChangeType type, String path, String fileContent){
        return new SCMChangeBuilder().with(scm -> {
            scm.path = path;
            scm.changedLines = lines;
            scm.changeType = type;
            scm.fileContent = fileContent;
        }).build();
    }

    private Change createChildPOMChange(Set<Integer> lines, ChangeType type ){
        return createPOMChange(lines, type, "src/test/resources/childPOM.xml", childPom);
    }
}
