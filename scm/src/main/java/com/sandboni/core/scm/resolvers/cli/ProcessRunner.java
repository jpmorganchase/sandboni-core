
package com.sandboni.core.scm.resolvers.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessRunner {

    private ProcessRunner() {
    }

    private static Process start(ProcessBuilder pb) throws IOException {
        try {
            return pb.start();
        } catch (IOException e) {
            throw new IOException("Unable to start sub-process", e);
        }
    }

    private static void waitFor(Process p, String[] command, List<String> outputLines) throws IOException {
        try {
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new IOException("command " + Arrays.toString(command) + " exited with code: " + exitCode + ". Output: " + outputLines.stream().collect(Collectors.joining(System.lineSeparator())));
            }
        } catch (InterruptedException e) {
            // Restore interrupted state
            Thread.currentThread().interrupt();
            throw new IOException("Error running process", e);
        }
    }

    public static List<String> runCommand(File workingDirectory, String... command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        if (workingDirectory != null) {
            pb.directory(workingDirectory);
        }
        pb.redirectErrorStream(true);
        Process p = start(pb);
        List<String> outputLines = getOutputLines(p);
        waitFor(p, command, outputLines);
        return outputLines;
    }

    private static List<String> getOutputLines(Process p) {
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return br.lines().collect(Collectors.toList());
    }

}
