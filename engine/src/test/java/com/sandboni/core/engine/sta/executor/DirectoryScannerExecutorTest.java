package com.sandboni.core.engine.sta.executor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DirectoryScannerExecutorTest {

    private DirectoryScannerExecutor directoryScannerExecutor;
    private Set<String> locations;

    @Before
    public void setUp() {
        locations = new HashSet<>();
        locations.add("location1");
        locations.add("location2");

        directoryScannerExecutor = new DirectoryScannerExecutor((location, files) -> {
            assertTrue(locations.contains(location));
        }, "DirectoryScannerExecutorTest");
    }

    @Test
    public void execute() {
        Collection<Map<String, Set<File>>> scanningResult = directoryScannerExecutor.execute(locations);
        assertEquals(2, scanningResult.size());
        scanningResult.forEach(map -> {
            assertTrue(locations.contains(map.keySet().iterator().next()));
        });
    }
}
