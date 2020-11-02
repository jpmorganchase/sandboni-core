package com.sandboni.core.engine.finder.scanner;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.executor.DirectoryScannerExecutor;
import com.sandboni.core.engine.sta.executor.ParallelExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.utils.TimeUtils.elapsedTime;

public class DirectoryScanner implements LocationScanner {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryScanner.class);
    static final String DEPENDENCY_JARS = "DependencyJars";

    private final String scannerName;

    public DirectoryScanner(String scannerName) {
        this.scannerName = scannerName;
    }

    @Override
    public Map<String, Set<File>> scan(ThrowingBiConsumer<String, Set<File>> consumer, Context context, boolean scanDependencies) {
        long start = System.nanoTime();
        logger.debug("[{}] {} Start traversing locations", Thread.currentThread().getName(), scannerName);

        List<String> allLocations = new ArrayList<>();
        allLocations.addAll(context.getTestLocations());
        allLocations.addAll(context.getSrcLocations());

        Map<String, Set<File>> locationFiles = new HashMap<>();
        Collection<Map<String, Set<File>>> locationToFilesFound = getDirectoryFinder(consumer).execute(allLocations);
        locationToFilesFound.forEach(map -> map.forEach(locationFiles::put));

        logger.debug("[{}] {} Finished traversing locations in {} milliseconds", Thread.currentThread().getName(), scannerName, elapsedTime(start));

        if (context.isEnablePreview() && scanDependencies) {
            locationFiles.put(DEPENDENCY_JARS, context.getDependencyJars().stream()
                    .map(File::new).collect(Collectors.toSet()));
        }

        return locationFiles;
    }

    ParallelExecutor<Collection<String>, Collection<Map<String, Set<File>>>> getDirectoryFinder(ThrowingBiConsumer<String, Set<File>> consumer) {
        return new DirectoryScannerExecutor(consumer, scannerName);
    }
}