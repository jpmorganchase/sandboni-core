package com.sandboni.core.scm.utils.timing;

public class StopWatch {
    private String name;
    private long startTime;
    private double totalTime;
    private double avgTime;
    private long executions;

    public StopWatch(String name) {
        this.name = name;
    }

    public StopWatch start() {
        synchronized (this) {
            startTime = System.currentTimeMillis();
            return this;
        }
    }

    public void stop() {
        synchronized (this) {
            long endTime = System.currentTimeMillis();
            long timeDiff = endTime - startTime;
            executions +=1;
            totalTime += timeDiff;
            avgTime = totalTime / executions;
        }
    }

    public String getName() {
        return name;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public long getExecutions() {
        return executions;
    }

    @Override
    public String toString() {
        return String.format("StopWatch[%s]:    executed %s times     totalTime=%.3f ms      avgTime=%.2f ms", name, executions, totalTime, avgTime);
    }
}
