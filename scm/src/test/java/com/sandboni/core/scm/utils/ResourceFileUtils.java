package com.sandboni.core.scm.utils;

import com.sandboni.core.scm.resolvers.cli.GitDiffRunnerTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceFileUtils {


    public static List<String> getResourceFileContentAsList(Class<? extends GitDiffRunnerTest> clazz, String fileName) throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        try(InputStream inputStream = classLoader.getResourceAsStream(fileName)){
            assert inputStream != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            return br.lines().collect(Collectors.toList());
        }
    }
}
