package com.sandboni.core.engine.sta.graph.vertex;

public final class VertexInitTypes {

    public static final Vertex START_VERTEX = new Vertex.Builder("test", "run").markSpecial().build();
    public static final Vertex END_VERTEX = new Vertex.Builder("scm", "diff").markSpecial().build();
    public static final Vertex DEAD_END_VERTEX = new Vertex.Builder("deadEnd", null).markSpecial().build();
    public static final Vertex CONTAINER_VERTEX = new Vertex.Builder("app", "contain").markSpecial().build();
    public static final Vertex CUCUMBER_VERTEX = new Vertex.Builder("cucumber", "contain").markSpecial().build();
    public static final Vertex CUCUMBER_RUNNER_VERTEX = new Vertex.Builder("cucumberRunner", "run").markSpecial().build();
    public static final Vertex TRACKING_VERTEX =  new Vertex.Builder("tracking", "ticket").markSpecial().build();
    public static final Vertex TEST_SUITE_VERTEX = new Vertex.Builder("testSuite", "run").markSpecial().build();
    public static final Vertex REFLECTION_CALL_VERTEX = new Vertex.Builder("reflectionCall", "run").markSpecial().build();

    private VertexInitTypes() {
        // constants
    }
}
