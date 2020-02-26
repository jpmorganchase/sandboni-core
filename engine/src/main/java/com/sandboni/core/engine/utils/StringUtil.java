package com.sandboni.core.engine.utils;

public class StringUtil {
    private StringUtil() {}

    public static boolean isEmptyOrNull(String str) {
        return str == null || str.isEmpty();
    }

    public static String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index);
        }
        return "";
    }
}
