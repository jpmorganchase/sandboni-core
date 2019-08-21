package com.sandboni.core.engine.sta.graph.vertex;

import com.sandboni.core.engine.finder.ExtensionType;

public class TestVertexHandler implements VertexHandler{


    @Override
    public void handle(Vertex v, String type, Object arg) {
        switch (type){
            case "Location":
                String loc = (String)arg;
                ((TestVertex)v).setExternalLocation(loc.contains(ExtensionType.JAR.type()));
                break;
            default:
        }

    }
}
