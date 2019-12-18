package com.sandboni.core.engine.scenario.lambda;

import com.sandboni.core.engine.common.CachingSupplier;

import java.util.function.Supplier;

public class Client {

    private final Supplier<Long> cachingClient = new CachingSupplier<>(this::getValueImpl);

    public Long getValue() {
        return cachingClient.get();
    }

    private Long getValueImpl() {
        int i = 0;
        return 100L;
    }

}
