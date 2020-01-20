package com.sandboni.core.engine.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
    private TimeUtils() {
        // static methods
    }

    public static long elapsedTime(long start) {
        return TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    }
}
