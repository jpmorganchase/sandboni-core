package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class AbstractGraphOperation<R extends OperationResult<?>> implements GraphOperation<R> {

    static final Collector<Vertex, ?, Map<String, Set<String>>> toMapActorAction = Collectors
            .groupingBy(Vertex::getActor, Collectors.mapping(Vertex::getAction, Collectors.toSet()));

    public static final String INVALID_ARGUMENT = "Input parameter can't be null";

    AbstractGraphOperation() {
    }
}
