package com.sandboni.core.engine.sta.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static com.sandboni.core.engine.utils.TimeUtils.elapsedTime;
import static java.util.stream.Collectors.toList;

public abstract class AbstractParallelExecutor<T, R> implements ParallelExecutor<Collection<T>, Collection<R>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractParallelExecutor.class);

    @Override
    public Collection<R> execute(Collection<T> input) {
        long start = System.nanoTime();

        List<CompletableFuture<R>> futures = input.stream()
                .map(getLoggableMappingFunction())
                .collect(toList());

        List<R> result = futures.stream()
                .map(CompletableFuture::join)
                .collect(toList());

        logger.debug("[{}] {} executed in {} milliseconds", Thread.currentThread().getName(), this.getExecutorName(), elapsedTime(start));

        return result;
    }

    abstract Function<T, R> getMappingFunction();

    protected Function<T, CompletableFuture<R>> getLoggableMappingFunction() {
        return inputItem -> {
            logger.debug("Accepting input {}", inputItem);
            return CompletableFuture.supplyAsync(() -> {
                logger.debug("Thread {} Processing inputItem {}", Thread.currentThread().getName(), inputItem);

                return getMappingFunction().apply(inputItem);
            }, getExecutorPool());
        };
    }

    protected Executor getExecutorPool() {
        return ExecutorPools.fixedThreadPool;
    }

    protected String getExecutorName() {
        return this.getClass().getSimpleName();
    }
}
