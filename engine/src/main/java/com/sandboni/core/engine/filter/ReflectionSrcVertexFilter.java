package com.sandboni.core.engine.filter;

import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;

public class ReflectionSrcVertexFilter implements VertexFilter {

    public Set<Vertex> filter(Map<LinkType, Boolean> adoptedLinkTypes, Stream<Link> allLinks) {
        return emptyIfFalse(adoptedLinkTypes.get(LinkType.REFLECTION_CALL_SRC) != null,
            () -> allLinks.filter(l -> LinkType.REFLECTION_CALL_SRC.equals(l.getLinkType())).map(Link::getCaller)
        );
    }

}

