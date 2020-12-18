package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.OperationResult;

import java.util.Map;
import java.util.Objects;

public class MapResult<K, V> implements OperationResult<Map<K, V>> {

    private final Map<K, V> result;

    MapResult(Map<K, V> result) {
        Objects.requireNonNull(result, "Result can't be null");
        this.result = result;
    }

    @Override
    public Map<K, V> get() {
        return result;
    }
}
