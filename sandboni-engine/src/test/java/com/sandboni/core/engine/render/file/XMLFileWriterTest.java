package com.sandboni.core.engine.render.file;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.xml.XMLFileRenderer;
import com.sandboni.core.engine.result.Result;
import com.sandboni.core.engine.result.ResultContent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class XMLFileWriterTest {
    private final static String MY_XML_TEST_FILE = "myXmlTest.xml";

    List<Employee> employeeList ;

    private final String RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<employee>\n" +
            "    <name>myName</name>\n" +
            "    <age>18</age>\n" +
            "    <pay>1000.0</pay>\n" +
            "    <projects>p1</projects>\n" +
            "    <projects>p2</projects>\n" +
            "</employee>";

    @Before
    public void beforeTest(){
        List<String> proj = new ArrayList<>();
        proj.add("p1");
        proj.add("p2");
        Employee employee = new Employee("myName", 18, 1000, proj);

        employeeList = new ArrayList<>();
        employeeList.add(employee);
    }

    @After
    public void after() throws IOException {
        Path path = Paths.get(System.getProperty("user.dir"), MY_XML_TEST_FILE);
        Files.deleteIfExists(path);
    }

    @Test
    public void basicTestXml() throws RendererException {
        Result res = new Result();
        res.put(ResultContent.OUTPUT_TO_FILE, List.class, employeeList);
        FileWriterEngine.write(res, new FileOptions.FileOptionsBuilder().with(fo -> {fo.name = MY_XML_TEST_FILE; fo.type = FileType.XML ;}).build());
        assertTrue(new File(System.getProperty("user.dir"), MY_XML_TEST_FILE).exists());
    }

    @Test
    public void testXMLRenderer() throws RendererException {
        XMLFileRenderer xmlFileRenderer = new XMLFileRenderer<>();
        Result res = new Result();
        res.put(ResultContent.OUTPUT_TO_FILE, List.class, employeeList);

        String result = xmlFileRenderer.renderBody(employeeList, new FileOptions.FileOptionsBuilder().with(fo -> {fo.name = "myXmlTest2.xml"; fo.type = FileType.XML ;}).build());
        Assert.assertNotNull(result);
        Assert.assertEquals(RESULT, result.trim());
    }
}
