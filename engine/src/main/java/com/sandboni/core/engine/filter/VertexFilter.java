package com.sandboni.core.engine.filter;

import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface VertexFilter {

    Set<Vertex> filter(Map<LinkType, Boolean> adoptedLinkTypes, Stream<Link> allLinks);
}
