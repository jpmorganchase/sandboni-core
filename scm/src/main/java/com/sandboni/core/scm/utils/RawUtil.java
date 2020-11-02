package com.sandboni.core.scm.utils;

import com.sandboni.core.scm.proxy.filter.FileExtensions;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class RawUtil {

    private static final String SLASH = "/";
    private static final String LINE_SEPARATOR = "\n";

    private static final String PACKAGE_REGEX = "package(.*);";
    private static final String MAIN_PATH = "src/main/";
    private static final String TEST_PATH = "src/test/";
    private static final String JAVA_PATH = "java/";

    private RawUtil() {}

    public static Set<Integer> getIntRange(int from, int to) {
        return IntStream.range(from, to)
                .boxed()
                .collect(Collectors.toSet());
    }

    public static String getFullClassPath(String content, String path) {
        if (content == null) {
            return removeSourceSet(path);
        }
        String packagePath = getPackageName(content);
        if (packagePath == null) {
            return removeSourceSet(path);
        }
        String packageName = packagePath + SLASH + lastElementBy(path, SLASH);
        return trimNonPackageFolders(path, packageName);
    }

    public static Set<String> grepKeys(String text, Set<Integer> changedLines) {
        final Set<String> keys = new HashSet<>();
        String[] lines = text.split(LINE_SEPARATOR);
        for (Integer lineNumber : changedLines) {
            populateKeys(keys, lines, lineNumber);
        }
        return keys;
    }

    private static void populateKeys(Set<String> keys, String[] lines, Integer lineNumber) {
        if (lineNumber > lines.length) return;

        String line = lines[lineNumber - 1];
        if (line.startsWith("#") || line.startsWith("!")) return;
        int keylength = line.indexOf('=');
        if ( keylength > 0)
            keys.add(line.substring(0, keylength));
    }

    private static String removeSourceSet(String path) {
        return path.replace(MAIN_PATH, "").replace(TEST_PATH, "").replace(JAVA_PATH, "");
    }

    private static String getPackageName(String fileContent) {
        Matcher matcher = Pattern.compile(PACKAGE_REGEX).matcher(fileContent);
        if (matcher.find()) {
            return matcher.group(1).trim().replace(".", SLASH);
        }
        return null;
    }

    private static String trimNonPackageFolders(String path, String packageName) {
        int index = path.indexOf(packageName);
        if (index > 0) {
            return path.substring(index);
        } else {
            return packageName + FileExtensions.JAVA.extension();
        }
    }

    private static String lastElementBy(String str, String c) {
        return last(str.split(c)).trim();
    }

    private static <T> T last(T[] array) {
        return array[array.length - 1];
    }
}

