package com.sandboni.core.engine.sta.graph.vertex;

public class VertexInitTypes {
    private VertexInitTypes() {}
    public static final Vertex START_VERTEX = new Vertex.Builder("test", "run", null).markSpecial().build();
    public static final Vertex END_VERTEX = new Vertex.Builder("scm", "diff", null).markSpecial().build();
    public static final Vertex DEAD_END_VERTEX = new Vertex.Builder("deadEnd", null, null).markSpecial().build();
    public static final Vertex CONTAINER_VERTEX = new Vertex.Builder("app", "contain", null).markSpecial().build();
    public static final Vertex CUCUMBER_VERTEX = new Vertex.Builder("cucumber", "contain", null).markSpecial().build();
    public static final Vertex TRACKING_VERTEX =  new Vertex.Builder("tracking", "ticket", null).markSpecial().build();
}
