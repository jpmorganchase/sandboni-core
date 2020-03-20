package com.sandboni.core.engine.finder.scanner;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.sta.Context;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface LocationScanner {

    Map<String, Set<File>> scan(ThrowingBiConsumer<String, Set<File>> consumer, Context context, boolean scanDependencies);
}
