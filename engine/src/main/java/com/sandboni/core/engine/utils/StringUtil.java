package com.sandboni.core.engine.utils;

public class StringUtil {
    private StringUtil() {}

    public static boolean isEmptyOrNull(String str) {
        return str == null || str.isEmpty();
    }
}
