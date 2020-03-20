package com.sandboni.core.engine.sta.executor;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.sandboni.core.engine.utils.TimeUtils.elapsedTime;

public class DirectoryScannerExecutor extends AbstractParallelExecutor<String, Map<String, Set<File>>> {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryScannerExecutor.class);

    private final ThrowingBiConsumer<String, Set<File>> consumer;
    private final String executorName;

    public DirectoryScannerExecutor(ThrowingBiConsumer<String, Set<File>> consumer, String executorName) {
        this.consumer = consumer;
        this.executorName = executorName;
    }

    @Override
    Function<String, Map<String, Set<File>>> getMappingFunction() {
        return location -> {
            logger.debug("[{}] {} Starting scanning for {}", Thread.currentThread().getName(), this.getExecutorName(), location);

            long start = System.nanoTime();

            Map<String, Set<File>> locationFiles = new HashMap<>();
            Set<File> testFiles = new HashSet<>();
            consumer.accept(location, testFiles);

            locationFiles.put(location, testFiles);

            logger.debug("[{}] {} {} executed in {} milliseconds", Thread.currentThread().getName(), this.getExecutorName(), location, elapsedTime(start));

            return locationFiles;
        };
    }

    @Override
    public String getExecutorName() {
        return this.getClass().getSimpleName() + "_" + executorName;
    }
}
