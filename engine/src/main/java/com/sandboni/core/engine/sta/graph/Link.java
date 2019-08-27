package com.sandboni.core.engine.sta.graph;

import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.util.Objects;

public class Link {
    private final Vertex caller;
    private final Vertex callee;
    private final LinkType linkType;

    public LinkType getLinkType() {
        return linkType;
    }

    Link(Vertex caller, Vertex callee, LinkType linkType) {
        this.caller = caller;
        this.callee = callee;
        this.linkType = linkType;
    }

    public void setFilter(String filter) {
        caller.setFilter(filter);
        callee.setFilter(filter);
    }

    public Vertex getCaller() {
        return caller;
    }

    public Vertex getCallee() {
        return callee;
    }

    @Override
    public String toString() {
        return linkType + " " + caller.toString() + " -> " + callee.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Link link = (Link) o;
        return Objects.equals(caller, link.caller) &&
                Objects.equals(callee, link.callee) &&
                linkType == link.linkType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(caller, callee, linkType);
    }
}