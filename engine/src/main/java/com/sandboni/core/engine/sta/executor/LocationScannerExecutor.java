package com.sandboni.core.engine.sta.executor;

import com.sandboni.core.engine.contract.ThrowingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LocationScannerExecutor implements ScannerExecutor {

    private static final Logger logger = LoggerFactory.getLogger(LocationScannerExecutor.class);

    private final ThrowingConsumer<String> consumer;

    public LocationScannerExecutor(ThrowingConsumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public List<String> scan(List<String> locations) {
        List<CompletableFuture<String>> filesFutures = locations.stream()
                .map(location -> {
                            logger.debug("Accepting location {}", location);
                            return CompletableFuture.supplyAsync(() -> {
                                logger.debug("Thread {} Processing location {}", Thread.currentThread().getName(), location);

                                consumer.accept(location);

                                return location;
                            }, ExecutorPools.fixedThreadPool);
                        }
                ).collect(Collectors.toList());

        return filesFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
