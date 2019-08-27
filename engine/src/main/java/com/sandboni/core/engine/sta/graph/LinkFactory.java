package com.sandboni.core.engine.sta.graph;

import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LinkFactory {

    private static Map<Vertex, Vertex> concurrentHashSet;

    static {
        concurrentHashSet = new ConcurrentHashMap<>();
    }

    private LinkFactory(){}

    public static Link createInstance(Vertex caller, Vertex callee, LinkType linkType) {
        return new Link(checkup(caller), checkup(callee), linkType);
    }

    private static Vertex checkup(Vertex v) {
        return concurrentHashSet.merge(v, v, LinkFactory::mergeVertex);
    }

    private static Vertex mergeVertex(Vertex oldVertex, Vertex newVertex) {
        if ( (Objects.isNull(oldVertex.getLocation()) || oldVertex.getLocation().isEmpty()) &&
                (Objects.nonNull(newVertex.getLocation()) && !newVertex.getLocation().isEmpty()))
            oldVertex.setLocation(newVertex.getLocation());
        return oldVertex;
    }

    public static void clear(){
        concurrentHashSet.clear();
    }
}
