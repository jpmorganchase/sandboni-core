package com.sandboni.core.engine.common;

import java.util.function.Supplier;

public class CachingSupplier<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T cachedValue;
    private volatile boolean computed = false;

    public CachingSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (!computed) {
            synchronized (this) {
                if(!computed) {
                    cachedValue = supplier.get();
                    computed = true;
                    return cachedValue;
                }
            }
        }
        return cachedValue;
    }
}