package com.sandboni.core.engine.finder;

import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.sta.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class FileTreeFinder implements Finder {
    private Map<String, ThrowingBiConsumer<File, Context>> consumers;
    private static final Logger log = LoggerFactory.getLogger(FileTreeFinder.class);

    protected abstract Map<String, ThrowingBiConsumer<File, Context>> getConsumers();

    public void find(Context context) {
        consumers = getConsumers();

        context.forEachLocation(location -> {
            log.info(String.format("Scanning location: %s; ", location));
            File f = new File(location);
            if (!f.exists()) {
                throw new IOException("File or folder [" + location + "] does not exist");
            }
            traverse(f, context);
        });
    }

    private void traverse(File f, Context context) {
        if (f.isFile()) {
            int index = f.getName().lastIndexOf('.');
            if (index > 0) {
                String ext = f.getName().substring(index);
                if(f.getName().contains("SuiteTestClass")) log.info(String.format("traverse: checking: %s; consumers.containsKey(ext)=", f.getName(), consumers.containsKey(ext)));
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
