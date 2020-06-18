package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.OperationResult;

import java.util.Objects;
import java.util.Set;

public class SetResult<E> implements OperationResult<Set<E>> {

    private final Set<E> result;

    SetResult(Set<E> result) {
        Objects.requireNonNull(result, "Result can't be null");
        this.result = result;
    }

    @Override
    public Set<E> get() {
        return result;
    }
}
