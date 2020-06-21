package com.sandboni.core.engine.contract;

import com.sandboni.core.engine.exception.ParseRuntimeException;

import java.util.function.Consumer;

// approach is taken from here
// http://codingjunkie.net/functional-iterface-exceptions/

@FunctionalInterface
@SuppressWarnings(value = {"squid:S00112","FunctionalInterfaceMethodChanged"})
public interface ThrowingConsumer<T> extends Consumer<T> {

    @Override
    default void accept(T t){
        try{
            acceptThrows(t);
        }catch (Exception e){
            throw new ParseRuntimeException(e);
        }
    }

    void acceptThrows(T t) throws Exception;
}
