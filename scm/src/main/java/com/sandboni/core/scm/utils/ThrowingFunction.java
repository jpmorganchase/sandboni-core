package com.sandboni.core.scm.utils;

import com.sandboni.core.scm.exception.SourceControlRuntimeException;

import java.util.function.Function;

@FunctionalInterface
@SuppressWarnings("squid:S00112")
public interface ThrowingFunction<T, R> extends Function<T,R> {

    @Override
    default R apply(T t) {
        try {
            return applyThrows(t);
        } catch (Exception e) {
            throw new SourceControlRuntimeException(e);
        }
    }

    R applyThrows(T t) throws Exception;
}
