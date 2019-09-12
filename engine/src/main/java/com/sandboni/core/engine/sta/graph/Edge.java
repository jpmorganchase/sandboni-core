package com.sandboni.core.engine.sta.graph;

import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.graph.DefaultEdge;

public class Edge extends DefaultEdge {

    private static final long serialVersionUID = -2441378649798526206L;
    private LinkType linkType;

    @Override
    public Vertex getSource() {
        return (Vertex) super.getSource();
    }

    @Override
    public Vertex getTarget() {
        return (Vertex) super.getTarget();
    }


    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    @Override
    public String toString() {
        return linkType == null ? "" : linkType.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Edge edge = (Edge) o;

        if (getLinkType() != edge.getLinkType()) {
            return false;
        }

        return (getSource() != null ? getSource().equals(edge.getSource()) : edge.getSource() == null) && (getTarget() != null ? getTarget().equals(edge.getTarget()) : edge.getTarget() == null);

    }

    @Override
    public int hashCode() {
        int result = getLinkType() != null ? getLinkType().hashCode() : 0;
        result = 31 * result + (getSource() != null ? getSource().hashCode() : 0);
        result = 31 * result + (getTarget() != null ? getTarget().hashCode() : 0);
        return result;
    }
}

