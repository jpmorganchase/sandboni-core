package com.sandboni.core.engine.render;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.FileOptions;
import com.sandboni.core.engine.render.file.FileType;
import com.sandboni.core.engine.render.file.csv.CSVFileStrategy;
import com.sandboni.core.engine.render.file.csv.RelatedTestsFileRenderer;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.render.file.csv.RelatedTestsFileRenderer.IS_DISCONNECTED_TESTS;

public class PresentationStrategyTest {

    protected static Set<TestVertex> tests = new HashSet<>();

    @BeforeClass
    public static void init() throws IOException {
        final String marshalledRelatedTests = "[" +
                "{\"actor\":\"com.sandboni.core.engine.result.ResultGeneratorTest\",\"action\":\"testResultWhenEmptyContextAndRunSelectiveModeIsTrue()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.result.ResultGeneratorTest\",\"action\":\"testResultWhenOnlyJavaContext()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.ProcessorTest\",\"action\":\"testGetUnreachableExitPoints()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.result.ResultGeneratorTest\",\"action\":\"testResultWhenOnlyJavaContextAndRunSelectiveModeIsTrue()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.result.ResultGeneratorTest\",\"action\":\"testResultWhenEmptyContext()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.ProcessorTest\",\"action\":\"testGetExitPoints()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.finder.bcel.BcelFinderTest\",\"action\":\"testPoCDiffChangeDetector()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.ProcessorTest\",\"action\":\"testFileRelatedTests()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.result.ResultGeneratorTest\",\"action\":\"testResultWhenBothBuildCnfgAndJavaContext()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.ProcessorTest\",\"action\":\"testGetDisconnectedVertices()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.ProcessorTest\",\"action\":\"testGetAllEntryPointsCount()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.ProcessorTest\",\"action\":\"testGetDisconnectedEntryPoints()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.result.ResultGeneratorTest\",\"action\":\"testResultWhenBothBuildCnfgAndJavaContextAndRunSelectiveModeIsTrue()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.result.ResultGeneratorTest\",\"action\":\"testResultWhenOnlyCnfgContextAndRunSelectiveModeIsTrue()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.result.ResultGeneratorTest\",\"action\":\"testResultWhenOnlyCnfgContext()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"com.sandboni.core.engine.ProcessorTest\",\"action\":\"testGetRelatedTests()\",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}," +
                "{\"actor\":\"Receive and process Cash Forecast messages of type cash management\",\"action\":\" \",\"isSpecial\":false,\"filePath\":null,\"filter\":null,\"ignore\":false,\"externalLocation\":false,\"lineNumbersEmpty\":true,\"special\":false}" +
                "]";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Set<HashMap<String, String>> tmp = mapper.readValue(marshalledRelatedTests, HashSet.class);

        for (HashMap<String, String> test : tmp) {
            TestVertex v = new TestVertex.Builder(test.get("actor"), test.get("action"), null).build();
            tests.add(v);
        }
    }

    @Test
    public void testPresentRelatedTests()  {
        FileOptions fileOptions = new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = "test.csv";
                    fo.type = FileType.CSV;
                    fo.attributes = "{"+IS_DISCONNECTED_TESTS +" :true}"; })
                .build();
        String result = new RelatedTestsFileRenderer().renderBody(tests, fileOptions);
        Assert.assertEquals(17 + 1/*for header*/, result.split("\n").length);
        Assert.assertEquals(5, result.split("\n")[17].split(",").length);
    }

    @Test
    public void testIsNotConnected()  {
        FileOptions fileOptions = new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = "test.csv";
                    fo.type = FileType.CSV;
                    fo.attributes = "{"+IS_DISCONNECTED_TESTS +" :true}"; })
                .build();
        String result = new RelatedTestsFileRenderer().renderBody(tests, fileOptions);
        Assert.assertEquals("N", result.split("\n")[17].split(",")[4]);
    }

    @Test
    public void testIstConnected()  {
        FileOptions fileOptions = new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = "test.csv";
                    fo.type = FileType.CSV;
                    fo.attributes = "{"+IS_DISCONNECTED_TESTS +" :false}"; })
                .build();
        String result = new RelatedTestsFileRenderer().renderBody(tests, fileOptions);
        Assert.assertEquals("Y", result.split("\n")[17].split(",")[4]);
    }

    @Test
    public void testIstConnectedNoAttr()  {
        FileOptions fileOptions = new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = "test.csv";
                    fo.type = FileType.CSV; })
                .build();
        String result = new RelatedTestsFileRenderer().renderBody(tests, fileOptions);
        Assert.assertEquals("Y", result.split("\n")[17].split(",")[4]);
    }

    @Test
    public void testPresentRelatedAndDisconnectedTests() throws RendererException {

        String header = new RelatedTestsFileRenderer().renderHeader();
        Assert.assertEquals("type,package,class,method,connected?\n", header);

        String result = new CSVFileStrategy<>(tests, new RelatedTestsFileRenderer(),
                new FileOptions.FileOptionsBuilder()
                        .with(fo -> {
                            fo.name = "test.csv";
                            fo.type = FileType.CSV;
                            fo.attributes = "{key1:val1, key2:val2}"; })
                        .build()).render();
        String[] lines = result.split("\n");
        Assert.assertTrue(lines.length > 1);

        Assert.assertEquals(18, lines.length);
        int relatedCount = (int) Arrays.stream(lines).filter(r -> r.endsWith("Y")).count();
        Assert.assertEquals(17, relatedCount);
        int disconnectedCount = (int) Arrays.stream(lines).filter(r -> r.endsWith("N")).count();
        Assert.assertEquals(0, disconnectedCount);

        boolean isHeader = true;
        for (String line : lines) {
            if (isHeader){
                isHeader = false;
                continue;
            }
            String[] columns = line.split(",");
            Assert.assertEquals(5, columns.length);
            Assert.assertTrue("Y".equals(columns[4].trim()) || "N".equals(columns[4].trim()));
        }

        int integrations = (int) Arrays.stream(lines).filter(l -> l.startsWith("INTEGRATION,")).count();
        int units = (int) Arrays.stream(lines).filter(l -> l.startsWith("UNIT,")).count();
        Assert.assertEquals(1, integrations);
        Assert.assertEquals(16, units);
    }
}
