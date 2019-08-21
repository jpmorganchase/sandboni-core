package com.sandboni.core.engine.sta.graph.vertex;

public class VertexHandlerFactory {

    private VertexHandlerFactory() {}

    static final String TEST_VERTEX_TYPE = "test";

    private static final TestVertexHandler testVertexHandler = new TestVertexHandler();

    public static VertexHandler getVertexHandler(String type){
        if (TEST_VERTEX_TYPE.equals(type)) {
            return testVertexHandler;
        }
        return (vtx, t, arg) -> { };
    }
}
