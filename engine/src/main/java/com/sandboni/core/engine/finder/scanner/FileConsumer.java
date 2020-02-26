package com.sandboni.core.engine.finder.scanner;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.executor.FileConsumerExecutor;
import com.sandboni.core.engine.sta.executor.ParallelExecutor;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileConsumer implements LocationConsumer {

    private final Map<String, ThrowingBiConsumer<File, Context>> consumers;
    private final Context context;
    private final String consumerName;

    public FileConsumer(Map<String, ThrowingBiConsumer<File, Context>> consumers, Context context, String consumerName) {
        this.consumers = new HashMap<>(consumers);
        this.context = context;
        this.consumerName = consumerName;
    }

    @Override
    public void consume(Map<String, Set<File>> locationFiles) {
        for (Map.Entry<String, Set<File>> entry : locationFiles.entrySet()) {
            String location = entry.getKey();
            Context localContext = context.getLocalContext(location);
            getFileConsumerExecutor(localContext).execute(entry.getValue());
            context.addLinks(localContext.getLinks());
        }

    }

    ParallelExecutor<Collection<File>, Collection<String>> getFileConsumerExecutor(Context localContext) {
        return new FileConsumerExecutor(consumers, localContext, consumerName);
    }
}
