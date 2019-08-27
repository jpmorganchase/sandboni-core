package com.sandboni.core.engine.sta.graph.vertex;

import com.sandboni.core.engine.finder.ExtensionType;

public class TestVertexHandler implements VertexHandler{


    @Override
    public void handle(Vertex v, String type, Object arg) {
        if ("Location".equals(type)) {
            String loc = (String) arg;
            ((TestVertex) v).setExternalLocation(loc.contains(ExtensionType.JAR.type()));
        }
    }
}
