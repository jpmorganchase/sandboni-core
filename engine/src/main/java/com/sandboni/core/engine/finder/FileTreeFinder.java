package com.sandboni.core.engine.finder;

import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class FileTreeFinder implements Finder {
    private static final Logger logger = LoggerFactory.getLogger(FileTreeFinder.class);

    private Map<String, ThrowingBiConsumer<File, Context>> consumers;

    protected abstract Map<String, ThrowingBiConsumer<File, Context>> getConsumers();

    public void find(Context context) {
        logger.info("[{}] Finder {} started", Thread.currentThread().getName(), this.getClass().getSimpleName());

        StopWatch sw1 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), "find", "getConsumers").start();
        consumers = getConsumers();
        sw1.stop();

        StopWatch sw2 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), "find", "traverseAllLocations").start();
        context.forEachLocation(location -> {
            File f = new File(location);
            if (!f.exists()) {
                throw new IOException("File or folder [" + location + "] does not exist");
            }
            traverse(f, context);
        }, scanDependencies());
        sw2.stop();

        logger.info("[{}] Finder {} finished", Thread.currentThread().getName(), this.getClass().getSimpleName());
    }

    protected boolean scanDependencies() {
        return false;
    }

    private void traverse(File f, Context context) {
        if (f.isFile()) {
            int index = f.getName().lastIndexOf('.');
            if (index > 0) {
                String ext = f.getName().substring(index);
                if (consumers.containsKey(ext)) {
                    consumers.get(ext).accept(f, context);
                }
            }
        } else if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File entry : files) {
                    traverse(entry, context);
                }
            }
        }
    }
}
