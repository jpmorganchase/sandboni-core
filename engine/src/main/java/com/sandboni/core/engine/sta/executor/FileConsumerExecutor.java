package com.sandboni.core.engine.sta.executor;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.sta.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.sandboni.core.engine.utils.StringUtil.getExtension;

public class FileConsumerExecutor extends AbstractParallelExecutor<File, String> {

    private Map<String, ThrowingBiConsumer<File, Context>> consumers;
    private Context context;
    private String executorName;

    public FileConsumerExecutor(Map<String, ThrowingBiConsumer<File, Context>> consumers, Context context, String executorName) {
        this.consumers = new HashMap<>(consumers);
        this.context = context;
        this.executorName = executorName;
    }

    @Override
    Function<File, String> getMappingFunction() {
        return input -> {
            ThrowingBiConsumer<File, Context> consumer = consumers.get(getExtension(input.getName()));
            if (consumer != null) {
                consumer.accept(input, context);
            }
            return input.getAbsolutePath();
        };
    }

    @Override
    public String getExecutorName() {
        return this.getClass().getSimpleName() + "_" + executorName;
    }
}
