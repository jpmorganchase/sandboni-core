package com.sandboni.core.scm.utils;

import java.util.Optional;

public final class FileUtil {

    private FileUtil() {}

    public static Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf('.') ));
    }
}
