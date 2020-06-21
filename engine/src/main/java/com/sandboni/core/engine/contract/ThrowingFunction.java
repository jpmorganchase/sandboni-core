package com.sandboni.core.engine.contract;

import com.sandboni.core.engine.exception.ParseRuntimeException;

import java.util.function.Function;

// approach is taken from here
// http://codingjunkie.net/functional-iterface-exceptions/

@FunctionalInterface
@SuppressWarnings(value = {"squid:S00112", "FunctionalInterfaceMethodChanged"})
public interface ThrowingFunction<T,R> extends Function<T,R> {

    @Override
    default R apply(T t){
        try{
            return applyThrows(t);
        }catch (Exception e){
            throw new ParseRuntimeException(e);
        }
    }

    R applyThrows(T t) throws Exception;
}
