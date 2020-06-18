package com.sandboni.core.engine.analyzer;

import com.sandboni.core.engine.filter.VertexFilterFactory;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.filter.VertexFilterFactory.VertexFilterTypes.REFLECTION_SRC;

public class ContextAnalyzer {

    private ContextAnalyzer() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger log = LoggerFactory.getLogger(ContextAnalyzer.class);

    public static boolean containsReflectionCallers(Context context) {
        Set<Vertex> reflectionCallers = VertexFilterFactory.get(REFLECTION_SRC).filter(context.getAdoptedLinkTypes(), context.getLinks());
        if (!reflectionCallers.isEmpty()) {
            String callers = reflectionCallers.stream().map(Vertex::getActor).collect(Collectors.joining(", "));
            log.info(" Reflection calls in source files found:\n{}", callers);
            return true;
        }
        return false;
    }
}
