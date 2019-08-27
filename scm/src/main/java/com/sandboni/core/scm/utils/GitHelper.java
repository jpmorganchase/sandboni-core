package com.sandboni.core.scm.utils;

import java.io.File;

public final class GitHelper {
    private GitHelper() { }

    public static String openCurrentFolder() {
        return new File(".").getAbsolutePath();
    }
}
