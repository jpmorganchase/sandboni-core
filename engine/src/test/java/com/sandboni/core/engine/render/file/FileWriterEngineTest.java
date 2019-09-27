package com.sandboni.core.engine.render.file;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.properties.PropertiesFileRenderer;
import com.sandboni.core.engine.render.file.writer.PropertiesFileWriter;
import com.sandboni.core.engine.result.ChangeDetectorResultMock;
import com.sandboni.core.engine.result.Result;
import com.sandboni.core.engine.result.ResultContent;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class FileWriterEngineTest {
    private final static String MY_TEST_FILE = "myTest.csv";
    private final static String MY_JSON_TEST_FILE = "myJsonTest.json";
    private final static String MY_PROP_TEST_FILE = "myPropTest.properties";
    private final static String MY_SANDBONI_JIRA_CONNECT_FILE = "sandboni-jira-connect.csv";
    private final static String MY_TEST_PROPERTIES_FILE_WRITER_FILE = "testPropertiesFileWriter.properties";

    private Path tempDir;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("output");
    }

    @After
    public void after() throws IOException {
        deleteFile(MY_TEST_FILE);
        deleteFile(MY_JSON_TEST_FILE);
        deleteFile(MY_PROP_TEST_FILE);
        deleteFile(MY_SANDBONI_JIRA_CONNECT_FILE);
        deleteFile("testPropertiesFileWriter.properties");
    }

    private void deleteFile(String fileName) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir"), fileName);
        Files.deleteIfExists(path);
    }

    @Test
    public void basicTest() throws RendererException {
        Set<TestVertex> set = new HashSet<>();
        set.add(new TestVertex.Builder("actor1.as", "action1a",null).build());
        set.add(new TestVertex.Builder("actor2bb", "action1b", null).build());

        Result res = new Result();
        res.put(ResultContent.RELATED_TESTS, Set.class, set);
        FileWriterEngine.write(set, new FileOptions.FileOptionsBuilder().with(fo -> {fo.name = MY_TEST_FILE; fo.type = FileType.CSV ;}).build());
        assertTrue(new File(System.getProperty("user.dir"), MY_TEST_FILE).exists());
    }

    @Test
    public void basicTestJson() throws RendererException {
        Set<TestVertex> set = new HashSet<>();
        set.add(new TestVertex.Builder("actor1.as", "action1a", null).build());
        set.add(new TestVertex.Builder("actor2bb", "action1b", null).build());


        Result res = new Result();
        res.put(ResultContent.RELATED_TESTS, Set.class, set);
        FileWriterEngine.write(set, new FileOptions.FileOptionsBuilder().with(fo -> {fo.name = MY_JSON_TEST_FILE; fo.type = FileType.JSON ;}).build());
        assertTrue(new File(System.getProperty("user.dir"), MY_JSON_TEST_FILE).exists());
    }

    @Test(expected = NullPointerException.class)
    public void testFileWriteWhenResultIsNull() throws RendererException {
        FileWriterEngine.write(null, new FileOptions.FileOptionsBuilder().with(fo -> {fo.name = MY_JSON_TEST_FILE; fo.type = FileType.JSON ;}).build());
    }

    @Test
    public void testBasicProperties() throws RendererException {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "val1");
        map.put("key2", "val2");

        Result res = new Result();
        res.put(ResultContent.OUTPUT_TO_FILE, Map.class, map);
        FileWriterEngine.write(map, new FileOptions.FileOptionsBuilder().with(fo -> {fo.name = MY_PROP_TEST_FILE; fo.type = FileType.PROPERTIES ;}).build());
        assertTrue(new File(System.getProperty("user.dir"), MY_PROP_TEST_FILE).exists());
    }

    @Test
    public void testPropertiesContent(){
        FileOptions fileOptions = new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = "test.properties";
                    fo.type = FileType.PROPERTIES; })
                .build();
        Map<String, String> map = new HashMap<>();
        map.put("key1", "val1");
        map.put("key2", "val2");
        Map result = new PropertiesFileRenderer<>().renderBody(map, fileOptions);

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.containsKey("key1"));
        Assert.assertTrue(result.containsKey("key2"));
    }

    @Test(expected = RendererException.class)
    public void testInvalidContent() throws RendererException {
        PropertiesFileWriter propertiesFileWriter = new PropertiesFileWriter();
        Path path = Paths.get(MY_TEST_PROPERTIES_FILE_WRITER_FILE);
        propertiesFileWriter.write(path, null);
    }

    @Test
    public void testPropertiesFileWriter() throws RendererException, IOException {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "val1");
        map.put("key2", "val2");

        PropertiesFileWriter propertiesFileWriter = new PropertiesFileWriter();
        Path path = Paths.get(MY_TEST_PROPERTIES_FILE_WRITER_FILE);
        propertiesFileWriter.write(path, map);

        try (InputStream input = new FileInputStream(path.toString())) {
            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            Assert.assertEquals(2, prop.size());
            Assert.assertTrue(prop.containsKey("key1"));
            Assert.assertTrue(prop.containsKey("key2"));
        }
        assertTrue(new File(System.getProperty("user.dir"), MY_TEST_PROPERTIES_FILE_WRITER_FILE).exists());
    }


    @Test
    public void testResultJiraTracking(){
        Processor processor = getProcessor("3", true);
        Result res = processor.getResultGenerator().generate(ResultContent.JIRA_TRACKING);
        Assert.assertTrue(res.isSuccess());
        // Assert.assertEquals(FilterIndicator.SELECTIVE, res.getFilterIndicator());
    }

    @Test
    public void testRelatedTestsToFile() throws IOException {
        Processor processor = getProcessor("3", true);
        Set<String> result = processor.getResultGenerator().generate(ResultContent.RELATED_TEST_TO_FILE).get();
        Assert.assertEquals("No Related Tests Found", 0, result.size());
        BufferedReader br = new BufferedReader(new FileReader(
                new File(String.format(ResultGenerator.TESTS_OUTPUT, tempDir, processor.getArguments().getOutputFormat()))));
        Object[] lines = br.lines().toArray();
        Assert.assertTrue("JSON file should be created",lines.length > 0);
    }

    private Processor getProcessor(String fromChangeId, boolean runSelective)  {
        return new ProcessorBuilder().with(procBuilder->{
                procBuilder.arguments = getArguments(fromChangeId, runSelective);
                procBuilder.gitDetector = new ChangeDetectorResultMock();
            }).build();
    }

    private Arguments getArguments(String fromChangeId, boolean runSelectiveModeIfBuildFileHasChanged) {

        return new ArgumentsBuilder().with($->{
            $.fromChangeId = fromChangeId;
            $.toChangeId = "999eee";
            $.repository = ".";
            $.selectiveMode = runSelectiveModeIfBuildFileHasChanged;
            $.outputFormat = "csv";
            $.reportDir = tempDir.toString();
            $.trackingSort = "key";
        }).build();
    }
}
