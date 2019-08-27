package com.sandboni.core.engine;

import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.result.Result;
import com.sandboni.core.engine.result.ResultContent;
import com.sandboni.core.engine.sta.Builder;
import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.GitHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ProcessorTest {

    private Processor processor;

    public ProcessorTest() {
        Arguments args = getArguments();
        processor = new Processor(args, new PoCDiffChangeDetector(), new Finder[]{}, new Connector[]{});
    }

    private Arguments getArguments() {
        return new ArgumentsBuilder().with($->{
            $.fromChangeId = "1";
            $.toChangeId = "2";
            $.repository = GitHelper.openCurrentFolder();
            $.filter = "com";
            $.stage = Arguments.BUILD_STAGE;
            $.coreCache = true;
            $.gitCache = true;
        }).build();
    }

    @Test
    public void testRun() {
        Map<String, Set<String>> result = processor.getResultGenerator().generate(ResultContent.ENTRY_POINTS).get();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetEntryPoints() {
        Map<String, Set<String>> result = processor.getResultGenerator().generate(ResultContent.ENTRY_POINTS).get();
        assertEquals("No entry points found", 0, result.size());
    }

    @Test
    public void getGraphBuilder() {
        Builder builder  = processor.getGraphBuilder();
        assertNotNull("Builder is not null", builder);
    }

    @Test
    public void testGetRelatedTests() {
        Set<Vertex> result = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS).get();
        assertEquals("No Related tests found", 0, result.size());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetWith2RCs() {
        processor.getResultGenerator().generate(ResultContent.RELATED_TESTS, ResultContent.DISCONNECTED_TESTS).get();
    }

    @Test
    public void testOneRCGet() {
        Result result = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS, ResultContent.DISCONNECTED_TESTS);
        assertNotNull(result);

        Assert.assertTrue(result.get(ResultContent.RELATED_TESTS) instanceof HashSet);
        assertEquals(0, ((HashSet)result.get(ResultContent.RELATED_TESTS)).size());

        Assert.assertTrue(result.get(ResultContent.DISCONNECTED_TESTS) instanceof HashSet);
        assertEquals(0, ((HashSet)result.get(ResultContent.DISCONNECTED_TESTS)).size());

    }

    @Test
    public void testGetDefaultProcessor() {
        Arguments args = getArguments();

        Processor p = new ProcessorBuilder()
                .with(procBuilder ->procBuilder.arguments = args)
                .build();

        assertNotNull(args.toString());
        assertNotNull("Default Processor is null", p);
    }

    @Test
    public void testCachedGetDefaultProcessor() {
        Arguments args = getArguments();

        Processor p = new ProcessorBuilder().with(procBuilder ->{
            procBuilder.arguments = args;
        }).build();

        assertNotNull(args.toString());
        assertNotNull("Default Processor is null", p);
    }

    @Test
    public void testGetDisconnectedEntryPoints() {
        Map<String, Set<String>> result = processor.getResultGenerator().generate(ResultContent.DISCONNECTED_ENTRY_POINTS).get();
        assertEquals("Should contain no disconnected entry points", 0, result.size());
    }

    @Test
    public void testGetDisconnectedVertices() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.DISCONNECTED_TESTS).get();
        assertEquals("Should contain no disconnected tests vertices", 0, result.size());
    }

    @Test
    public void testOutputMetadataLinks() {
        processor.getGraphBuilder().outputMetadataLinks();
        assertTrue(new File(System.getProperty("user.dir"), "JiraReport.csv").exists());
    }

    @Test
    public void testGetUnreachableExitPoints() {
        Map<String, Set<String>> result = processor.getResultGenerator().generate(ResultContent.UNREACHABLE_EXIT_POINTS).get();
        assertEquals("Should contain no disconnected tests vertices", 0, result.size());
    }

    @Test
    public void testGetExitPoints() {
        Map<String, Set<String>> result = processor.getResultGenerator().generate(ResultContent.EXIT_POINTS).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetAllEntryPoints() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.ALL_TESTS).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @After
    public void after() throws IOException {
        Path path = Paths.get(System.getProperty("user.dir"), "JiraReport.csv");
        Files.deleteIfExists(path);
    }
}