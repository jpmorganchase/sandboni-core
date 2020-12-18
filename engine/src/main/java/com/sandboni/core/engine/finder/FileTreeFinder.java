package com.sandboni.core.engine.finder;

import com.sandboni.core.engine.sta.Finder;
import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.finder.scanner.DirectoryScanner;
import com.sandboni.core.engine.finder.scanner.FileConsumer;
import com.sandboni.core.engine.finder.scanner.LocationConsumer;
import com.sandboni.core.engine.finder.scanner.LocationScanner;
import com.sandboni.core.engine.sta.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.sandboni.core.engine.utils.StringUtil.getExtension;
import static com.sandboni.core.engine.utils.TimeUtils.elapsedTime;

public abstract class FileTreeFinder implements Finder {
    private static final Logger logger = LoggerFactory.getLogger(FileTreeFinder.class);

    private Map<String, ThrowingBiConsumer<File, Context>> consumers;

    protected abstract Map<String, ThrowingBiConsumer<File, Context>> getConsumers();

    public void find(Context context) {
        long start = System.nanoTime();
        logger.debug("[{}] Finder {} started", Thread.currentThread().getName(), this.getClass().getSimpleName());

        consumers = getConsumers();

        Map<String, Set<File>> locationFiles = getLocationScanner().scan((location, files) -> {
            File f = new File(location);
            if (!f.exists()) {
                throw new IOException("File or folder [" + location + "] does not exist");
            }
            traverse(f, files);
        }, context, scanDependencies());

        getLocationConsumer(context).consume(locationFiles);

        logger.debug("[{}] Finder {} finished in {} milliseconds", Thread.currentThread().getName(), this.getClass().getSimpleName(), elapsedTime(start));
    }

    LocationScanner getLocationScanner() {
        return new DirectoryScanner(this.getClass().getSimpleName());
    }

    LocationConsumer getLocationConsumer(Context context) {
        return new FileConsumer(consumers, context, this.getClass().getSimpleName());
    }

    protected boolean scanDependencies() {
        return false;
    }

    // collect the list of files to be consumed
    // later parallelize the consumption of each file
    private void traverse(File file, Set<File> filesToScan) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File entry : files) {
                    traverse(entry, filesToScan);
                }
            }
        } else {
            if (consumers.containsKey(getExtension(file.getName()))) {
                logger.debug("Adding file: {}", file.getAbsolutePath());
                filesToScan.add(file);
            }
        }
    }


}
