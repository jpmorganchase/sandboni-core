package com.sandboni.core.engine.finder.scanner;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.executor.ParallelExecutor;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class DirectoryScannerTest {

    private DirectoryScanner directoryScanner;

    @Before
    public void setUp() {
        directoryScanner = new DirectoryScanner("DirectoryScannerTest") {
            @Override
            ParallelExecutor<Collection<String>, Collection<Map<String, Set<File>>>> getDirectoryFinder(ThrowingBiConsumer<String, Set<File>> consumer) {
                return new DirectoryScannerExecutorMock();
            }
        };
    }

    @Test
    public void scanAllLocations() {
        String[] srcLocation = new String[]{"srcLocation1", "srcLocation2"};
        String[] testLocation = new String[]{"testLocation1", "testLocation2"};
        String[] dependencies = new String[]{"dependency1", "dependency2"};
        Set<String> allLocations = new HashSet<>();
        allLocations.addAll(Stream.of(srcLocation).map(location -> new File(location).getAbsolutePath()).collect(Collectors.toSet()));
        allLocations.addAll(Stream.of(testLocation).map(location -> new File(location).getAbsolutePath()).collect(Collectors.toSet()));
        allLocations.add(DirectoryScanner.DEPENDENCY_JARS);

        Context context = new Context("appId", srcLocation, testLocation, dependencies, "", new ChangeScopeImpl(), null, null, true);

        Map<String, Set<File>> scanResult = directoryScanner.scan((location, files) -> {
            // not called because is not running parallel executor
        }, context, true);


        assertEquals(5, scanResult.size());
        allLocations.forEach(location -> {
            assertTrue(scanResult.containsKey(location));
            assertFalse(scanResult.get(location).isEmpty());
        });
    }

    @Test
    public void skipDependencyLocations() {
        String[] srcLocation = new String[]{"srcLocation1", "srcLocation2"};
        String[] testLocation = new String[]{"testLocation1", "testLocation2"};
        String[] dependencies = new String[]{"dependency1", "dependency2"};
        Set<String> allLocations = new HashSet<>();
        allLocations.addAll(Stream.of(srcLocation).map(location -> new File(location).getAbsolutePath()).collect(Collectors.toSet()));
        allLocations.addAll(Stream.of(testLocation).map(location -> new File(location).getAbsolutePath()).collect(Collectors.toSet()));
        allLocations.add(DirectoryScanner.DEPENDENCY_JARS);

        Context context = new Context("appId", srcLocation, testLocation, dependencies, "", new ChangeScopeImpl(), null, null, true);

        Map<String, Set<File>> scanResult = directoryScanner.scan((location, files) -> {
            // not called because is not running parallel executor
        }, context, false);


        assertEquals(4, scanResult.size());
        allLocations.forEach(location -> {
            if (!location.equals(DirectoryScanner.DEPENDENCY_JARS)) {
                assertTrue(scanResult.containsKey(location));
                assertFalse(scanResult.get(location).isEmpty());
            }
        });
    }

    private static class DirectoryScannerExecutorMock implements ParallelExecutor<Collection<String>, Collection<Map<String, Set<File>>>> {

        @Override
        public Collection<Map<String, Set<File>>> execute(Collection<String> input) {
            List<Map<String, Set<File>>> result = new ArrayList<>();
            input.forEach(location -> {
                Map<String, Set<File>> map = new HashMap<>();
                Set<File> files = new HashSet<>();
                files.add(new File("file1"));
                map.put(location, files);
                result.add(map);
            });
            return result;
        }
    }

    @Test
    public void dependencyScanWhenEnablePreviewFalse() {
        String[] srcLocation = new String[]{"srcLocation1", "srcLocation2"};
        String[] testLocation = new String[]{"testLocation1", "testLocation2"};
        String[] dependencies = new String[]{"dependency1", "dependency2"};
        Set<String> allLocations = new HashSet<>();
        allLocations.addAll(Stream.of(srcLocation).map(location -> new File(location).getAbsolutePath()).collect(Collectors.toSet()));
        allLocations.addAll(Stream.of(testLocation).map(location -> new File(location).getAbsolutePath()).collect(Collectors.toSet()));

        Context context = new Context("appId", srcLocation, testLocation, dependencies, "", new ChangeScopeImpl(), null, null, false);

        Map<String, Set<File>> scanResult = directoryScanner.scan((location, files) -> {
            // not called because is not running parallel executor
        }, context, true);


        assertEquals(4, scanResult.size());
        allLocations.forEach(location -> {
            assertTrue(scanResult.containsKey(location));
            assertFalse(scanResult.get(location).isEmpty());
        });
    }
}