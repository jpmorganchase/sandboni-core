package com.sandboni.core.engine.render.file;

import java.util.Map;

@FunctionalInterface
public interface RowFormatter<V> {

    V format(Object row, Map<String, String> attributes);

}
