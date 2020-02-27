package com.sandboni.core.engine.sta.executor;

import com.sandboni.core.engine.contract.ThrowingConsumer;

import java.util.function.Function;

public class LocationScannerExecutor extends AbstractParallelExecutor<String, String> {

    private final ThrowingConsumer<String> consumer;

    public LocationScannerExecutor(ThrowingConsumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    Function<String, String> getMappingFunction() {
        return input -> {
            consumer.accept(input);
            return input;
        };
    }
}
