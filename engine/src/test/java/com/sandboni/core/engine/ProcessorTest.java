package com.sandboni.core.engine;

import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.filter.MockChangeScopeFilter;
import com.sandboni.core.engine.result.Result;
import com.sandboni.core.engine.result.ResultContent;
import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.GitHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ProcessorTest {

    private static final String JAVA_CLASS_PATH = "java.class.path";
    private Processor processor;

    public ProcessorTest() {
        Arguments args = getArguments();
        processor = new Processor(args, new PoCDiffChangeDetector(), new Finder[]{}, new Connector[]{}, new MockChangeScopeFilter());
    }

    private Arguments getArguments() {
        return Arguments.builder()
                .srcLocation(new String[]{"."})
                .applicationId("sandboni.default")
                .fromChangeId("1")
                .toChangeId("2")
                .repository(GitHelper.openCurrentFolder())
                .filter("com")
                .stage(Stage.BUILD.name())
                .coreCache(true)
                .gitCache(true)
                .enablePreview(true)
                .build();
    }

    @Test
    public void testRun() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS).get();
        assertNotNull(result);
        assertTrue(result.isEmpty());
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
    public void testGetDisconnectedTests() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.DISCONNECTED_TESTS).get();
        assertEquals("Should contain no disconnected entry points", 0, result.size());
    }

    @Test
    public void testGetDisconnectedVertices() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.DISCONNECTED_TESTS).get();
        assertEquals("Should contain no disconnected tests vertices", 0, result.size());
    }

    @Test
    public void testGetUnreachableChanges() {
        Map<String, Set<String>> result = processor.getResultGenerator().generate(ResultContent.UNREACHABLE_CHANGES).get();
        assertEquals("Should contain no disconnected tests vertices", 0, result.size());
    }

    @Test
    public void testGetChanges() {
        Map<String, Set<String>> result = processor.getResultGenerator().generate(ResultContent.CHANGES).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetAllTests() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.ALL_TESTS).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetAllExternalTests() {
        Result result = processor.getResultGenerator().generate(ResultContent.ALL_EXTERNAL_CUCUMBER, ResultContent.ALL_EXTERNAL_UNIT);
        Set<TestVertex> cukes = result.get(ResultContent.ALL_EXTERNAL_CUCUMBER);
        Set<TestVertex> ut = result.get(ResultContent.ALL_EXTERNAL_UNIT);
        assertEquals("Should contain no external tests", 0, cukes.size() + ut.size());
    }

    @Test
    public void testJavaClassPathIsNotModified() {
        String currentJavaClasspath = System.getProperty(JAVA_CLASS_PATH, "");
        processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        String afterExecJavaClasspath = System.getProperty(JAVA_CLASS_PATH, "");
        assertEquals(currentJavaClasspath, afterExecJavaClasspath);
    }

    @Test
    public void testGetRelatedUnitTests() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.RELATED_UNIT).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetRelatedCucumberTests() {
        Set<CucumberVertex> result = processor.getResultGenerator().generate(ResultContent.RELATED_CUCUMBER).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetDisconnectedUnitTests() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.DISCONNECTED_UNIT).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetDisconnectedCucumberTests() {
        Set<CucumberVertex> result = processor.getResultGenerator().generate(ResultContent.DISCONNECTED_CUCUMBER).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetRelatedExternalUnitTests() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.RELATED_EXTERNAL_UNIT).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetRelatedExternalCucumberTests() {
        Set<CucumberVertex> result = processor.getResultGenerator().generate(ResultContent.RELATED_EXTERNAL_CUCUMBER).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetAllExternalUnitTests() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.ALL_EXTERNAL_UNIT).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetAllExternalCucumberTests() {
        Set<CucumberVertex> result = processor.getResultGenerator().generate(ResultContent.ALL_EXTERNAL_CUCUMBER).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetIncludedByAnnotationTests() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.INCLUDED_BY_ANNOTATION).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void testGetCucumberRunnersTests() {
        Set<TestVertex> result = processor.getResultGenerator().generate(ResultContent.CUCUMBER_RUNNERS).get();
        assertEquals("Should contain no exit points", 0, result.size());
    }

    @Test
    public void enablePreview() {
        assertTrue(processor.getArguments().isEnablePreview());
    }

    @Test
    public void filter() {
        assertEquals("com", processor.getArguments().getFilter());
    }
}