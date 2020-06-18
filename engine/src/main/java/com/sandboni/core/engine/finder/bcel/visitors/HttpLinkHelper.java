package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HttpLinkHelper {

    private HttpLinkHelper() {
    }


    public static void addHttpLinks(String verb, Context context, String methodPath, String className, String methodName, boolean controllerSide) {
        addHttpLinks(new HashSet<>(Collections.singletonList(verb)), context, methodPath, className, methodName, controllerSide);
    }

    public static void addHttpLinks(Set<String> verbs, Context context, String methodPath, String className, String methodName, boolean controllerSide) {
        Set<String> resultingVerbs = verbs.isEmpty() ? HttpConsts.getHttpVerb() : verbs;

        if (!methodPath.isEmpty()) {
            resultingVerbs.forEach(m -> {
                Vertex httpVertex = new Vertex.Builder( m + " " + HttpConsts.HTTP_LOCALHOST, methodPath, context.getCurrentLocation())
                        .markSpecial()
                        .build();

                Vertex currentVertex = new Vertex.Builder(className, methodName, context.getCurrentLocation()).build();

                if (controllerSide) {
                    context.addLink(LinkFactory.createInstance(context.getApplicationId(), httpVertex, currentVertex, LinkType.HTTP_HANLDER));

                    // implicit initialization will happen
                    context.addLink(LinkFactory.createInstance(context.getApplicationId(), httpVertex,
                            new Vertex.Builder(className, MethodUtils.INIT).build(),
                            LinkType.METHOD_CALL));
                    context.addLink(LinkFactory.createInstance(context.getApplicationId(), httpVertex,
                            new Vertex.Builder(className, MethodUtils.CLINIT).build(),
                            LinkType.STATIC_CALL));
                } else
                    context.addLinks(LinkFactory.createInstance(context.getApplicationId(), currentVertex,
                            httpVertex, LinkType.HTTP_REQUEST));
            });
        }
    }

}
