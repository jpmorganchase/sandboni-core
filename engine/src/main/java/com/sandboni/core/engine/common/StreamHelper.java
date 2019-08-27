package com.sandboni.core.engine.common;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamHelper {

    private StreamHelper() {
    }

    public static <T extends B, B> Function<B, Stream<T>> ofType(Class<T> targetType) {
        return value -> targetType.isInstance(value) ? Stream.of(targetType.cast(value)) : Stream.empty();
    }

    public static <T> Set<T> emptyIfFalse(boolean condition, Supplier<Stream<T>> supplier) {
        if (!condition) {
            return new HashSet<>();
        }
        return supplier.get().collect(Collectors.toSet());
    }
}
