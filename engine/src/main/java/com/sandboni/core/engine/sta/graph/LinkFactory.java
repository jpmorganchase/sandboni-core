package com.sandboni.core.engine.sta.graph;

import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LinkFactory {

    private static ConcurrentHashMap<String, ConcurrentHashMap<Vertex, Vertex>> linkFactories = new ConcurrentHashMap<>();

    private LinkFactory(){}

    public static Link createInstance(String factoryId, Vertex caller, Vertex callee, LinkType linkType) {
        return new Link(checkup(factoryId, caller), checkup(factoryId, callee), linkType);
    }

    private static Vertex checkup(String factoryId, Vertex v) {
        return linkFactories.computeIfAbsent(factoryId, key -> new ConcurrentHashMap<>())
                .merge(v, v, LinkFactory::mergeVertex);
    }

    private static Vertex mergeVertex(Vertex oldVertex, Vertex newVertex) {
        if ( (Objects.isNull(oldVertex.getLocation()) || oldVertex.getLocation().isEmpty()) &&
                (Objects.nonNull(newVertex.getLocation()) && !newVertex.getLocation().isEmpty()))
            oldVertex.setLocation(newVertex.getLocation());
        return oldVertex;
    }

    public static void clear(String factoryId){
        if (factoryId == null) {
            return;
        }
        linkFactories.remove(factoryId);
    }
}
