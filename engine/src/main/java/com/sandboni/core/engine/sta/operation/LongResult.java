package com.sandboni.core.engine.sta.operation;

import java.util.Objects;

public class LongResult implements OperationResult<Long> {

    private final Long result;

    LongResult(Long result) {
        Objects.requireNonNull(result, "Result can't be null");
        this.result = result;
    }

    @Override
    public Long get() {
        return result;
    }
}
