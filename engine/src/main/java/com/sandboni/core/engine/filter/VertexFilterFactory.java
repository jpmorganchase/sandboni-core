package com.sandboni.core.engine.filter;

import java.util.EnumMap;

public class VertexFilterFactory {

    public enum VertexFilterTypes {REFLECTION_SRC}

    private static EnumMap<VertexFilterTypes, VertexFilter> filters = new EnumMap<>(VertexFilterTypes.class);

    static {
        filters.put(VertexFilterTypes.REFLECTION_SRC, new ReflectionSrcVertexFilter());
    }

    public static VertexFilter get(VertexFilterTypes type) {
        return filters.get(type);
    }
}
