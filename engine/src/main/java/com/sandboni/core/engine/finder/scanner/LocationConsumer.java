package com.sandboni.core.engine.finder.scanner;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface LocationConsumer {

    void consume(Map<String, Set<File>> locationFiles);
}
