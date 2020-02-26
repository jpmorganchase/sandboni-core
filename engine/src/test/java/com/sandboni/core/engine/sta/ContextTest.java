package com.sandboni.core.engine.sta;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContextTest {

    public static final String JAVA_CLASS_PATH = "java.class.path";
    private Context context;
    private String[] sourceLocations;
    private String[] testLocations;

    @Before
    public void setUp() {
        sourceLocations = new String[]{"./target/classes"};
        testLocations = new String[]{"./target/test-classes"};
        context = new Context(sourceLocations, testLocations, "com,org", null, null);
    }

    @Test
    public void classPathValueFromLocations() {
        List<String> srcItems = getPathItems(sourceLocations);
        srcItems.forEach(item -> assertTrue(context.getClassPath().contains(item)));

        List<String> testItems = getPathItems(testLocations);
        testItems.forEach(item -> assertTrue(context.getClassPath().contains(item)));
    }

    @Test
    public void classPathValueFromSystemProperty() {
        String currentJavaClasspath = System.getProperty(JAVA_CLASS_PATH, "");
        Set<String> projectClasspath = new HashSet<>(Arrays.asList(currentJavaClasspath.split(File.pathSeparator)));

        projectClasspath.forEach(item -> assertTrue(context.getClassPath().contains(item)));
    }

    private List<String> getPathItems(String[] sourceLocations) {
        return Arrays.stream(sourceLocations)
                .map(l -> new File(l).getAbsolutePath())
                .collect(Collectors.toList());
    }

    @Test
    public void enablePreview() {
        assertTrue(context.isEnablePreview());
        assertTrue(context.getLocalContext().isEnablePreview());
    }

    @Test
    public void filterValue() {
        assertTrue(context.getFilters().contains("com"));
        assertTrue(context.getFilters().contains("org"));

        assertTrue(context.getLocalContext().getFilters().contains("com"));
        assertTrue(context.getLocalContext().getFilters().contains("org"));
    }

    @Test
    public void localContextWithSameLocation() {
        assertTrue(context.getCurrentLocation().isEmpty());
        assertEquals(context.getCurrentLocation(), context.getLocalContext().getCurrentLocation());
        assertEquals("NewLocation", context.getLocalContext("NewLocation").getCurrentLocation());
    }
}
