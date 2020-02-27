package com.sandboni.core.engine.sta.executor;

public interface ParallelExecutor<T, R> {

    R execute(T input);

}
