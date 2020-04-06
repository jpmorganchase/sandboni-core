package com.sandboni.core.engine.filter;

import java.util.HashMap;

public class VertexFilterFactory {

    public enum VertexFilterTypes {REFLECTION_SRC}

    private static HashMap<VertexFilterTypes, VertexFilter> filters = new HashMap<>();

    static {
        filters.put(VertexFilterTypes.REFLECTION_SRC, new ReflectionSrcVertexFilter());
    }

    public static VertexFilter get(VertexFilterTypes type) {
        return filters.get(type);
    }
}
