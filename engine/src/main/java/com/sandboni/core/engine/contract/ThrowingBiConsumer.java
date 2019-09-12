package com.sandboni.core.engine.contract;

import com.sandboni.core.engine.exception.ParseRuntimeException;

import java.util.function.BiConsumer;

// approach is taken from here
// http://codingjunkie.net/functional-iterface-exceptions/

@FunctionalInterface
@SuppressWarnings("squid:S00112")
public interface ThrowingBiConsumer<T,U> extends BiConsumer<T,U> {

    @Override
    default void accept(T t, U u){
        try{
            acceptThrows(t, u);
        }catch (Exception e){
            throw new ParseRuntimeException(e);
        }
    }

    void acceptThrows(T t, U u) throws Exception;
}
