package com.sandboni.core.engine.sta.graph.vertex;

public final class VertexInitTypes {

    public static final Vertex START_VERTEX = new Vertex.Builder("test", "run", null).markSpecial().build();
    public static final Vertex END_VERTEX = new Vertex.Builder("scm", "diff", null).markSpecial().build();
    public static final Vertex DEAD_END_VERTEX = new Vertex.Builder("deadEnd", null, null).markSpecial().build();
    public static final Vertex CONTAINER_VERTEX = new Vertex.Builder("app", "contain", null).markSpecial().build();
    public static final Vertex CUCUMBER_VERTEX = new Vertex.Builder("cucumber", "contain", null).markSpecial().build();
    public static final Vertex CUCUMBER_RUNNER_VERTEX = new Vertex.Builder("cucumberRunner", "run", null).markSpecial().build();
    public static final Vertex TRACKING_VERTEX =  new Vertex.Builder("tracking", "ticket", null).markSpecial().build();

    public static final Vertex TEST_SUITE_VERTEX = new Vertex.Builder("testSuite", "run", null).markSpecial().build();

    private VertexInitTypes() {
        // constants
    }
}
