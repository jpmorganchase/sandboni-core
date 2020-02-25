package com.sandboni.core.scm.utils.timing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class StopWatchManager {

    private static final Logger log = LoggerFactory.getLogger(StopWatchManager.class);

    private static ConcurrentHashMap<String, StopWatch> stopWatchesRepo = new ConcurrentHashMap<>();

    private static StopWatch getStopWatch(String name) {
        return stopWatchesRepo.computeIfAbsent(name, StopWatch::new);
    }

    public static StopWatch getStopWatch(String className, String methodName, String action) {
        return getStopWatch(getFormattedStopWatchName(className, methodName, action));
    }

    private static String getFormattedStopWatchName(String className, String methodName, String action) {
        return className + " --> " + methodName + " --> " + action;
    }

    public static void flushAll() {
        log.debug(getFormattedData());
    }

    public static void flushAllAboveThreshold(double totalTimeThreshold) {
        log.debug(getFormattedData(totalTimeThreshold));
    }

    public static String getFormattedData(){
        return getFormattedData(-1);
    }

    public static String getFormattedData(double totalTimeThreshold) {
        String rowFormat = "%-90s| %-15s| %-15s| %-15s";
        StringBuilder sb = new StringBuilder(String.format("StopWatch statistics (Found: %s):", stopWatchesRepo.mappingCount()));
        sb.append("\n");
        sb.append(String.format(rowFormat, "Name", "Times Executed", "Total Time(ms)", "Avg Time(ms)"));
        sb.append("\n");
        sb.append("--------------------------------------------------------------------------------------------------------------------------------");
        sb.append("\n");

        Stream<StopWatch> filteredSW = stopWatchesRepo.values().stream().sorted(Comparator.comparing(StopWatch::getName));
        if(totalTimeThreshold > 0) filteredSW = filteredSW.filter(sw -> sw.getTotalTime() > totalTimeThreshold);
        filteredSW.forEach(sw -> sb.append(formatRowData(rowFormat, sw)).append("\n"));
        sb.append("--------------------------------------------------------------------------------------------------------------------------------");
        return sb.toString();
    }

    private static String formatRowData(String rowFormat, StopWatch sw) {
        String avgTime = String.format("%.3f", sw.getAvgTime());
        return String.format(rowFormat, sw.getName(), sw.getExecutions(), sw.getTotalTime(), avgTime);
    }

}
