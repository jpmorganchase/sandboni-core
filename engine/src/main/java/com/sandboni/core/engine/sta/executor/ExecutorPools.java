package com.sandboni.core.engine.sta.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ExecutorPools {

    // By default we double the number of available cores
    private static final int THREAD_NUMBER = Runtime.getRuntime().availableProcessors() * 2;

    static final Executor fixedThreadPool =
            Executors.newFixedThreadPool(THREAD_NUMBER,
                    (Runnable r) -> {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        return thread;
                    });

    private ExecutorPools() {
        // static elements
    }
}
