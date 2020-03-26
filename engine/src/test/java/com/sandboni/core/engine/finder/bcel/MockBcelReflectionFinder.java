package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.io.IOException;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.REFLECTION_CALL_VERTEX;

public class MockBcelReflectionFinder extends MockBcelReflectionEmptyFinder {

    @Override
    public void find(Context context) throws IOException {
        Vertex v = new Vertex.Builder("ClassWithReflectionCall", "reflection ref", context.getCurrentLocation()).build();
        context.addLink(LinkFactory.createInstance(context.getApplicationId(), v, REFLECTION_CALL_VERTEX, LinkType.REFLECTION_CALL_SRC));
    }

}
