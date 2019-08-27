package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes;
import org.apache.bcel.classfile.Method;

import java.util.Arrays;
import java.util.Objects;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;

/**
 * Visit Java classes that contains method calls to other class members.
 * Note: This class is not thread safe.
 */
public class CallerClassVisitor extends ClassVisitorBase implements ClassVisitor {

    private CallerFieldOrMethodVisitor[] getVisitors(Method m) {
        return new CallerFieldOrMethodVisitor[]{
                new CallerMethodVisitor(m, javaClass, context),
                new CallerFieldVisitor(m, javaClass, context),
                new AnonymousClassMethodVisitor(m, javaClass, context),
                new ThreadRunMethodVisitor(m, javaClass, context),
                new CucumberMethodVisitor(m, javaClass, context)};
    }

    @Override
    public void visitMethod(Method method) {
        long linksAdded = Arrays.stream(getVisitors(method))
                .map(CallerFieldOrMethodVisitor::start)
                .mapToInt(i -> i).sum();

        // in order to detect locations (modules) each method has to have at least one entry in the graph as caller
        if (linksAdded == 0) {
            Vertex v ;
            if (Objects.nonNull(context.getCurrentLocation()) && context.getTestLocations().contains(context.getCurrentLocation())){
                v = new TestVertex.Builder(this.javaClass.getClassName(), MethodUtils.formatMethod(method), context.getCurrentLocation()).build();
            }else{
                v = new Vertex.Builder(this.javaClass.getClassName(), MethodUtils.formatMethod(method), context.getCurrentLocation())
                        .build();
            }
            context.addLink(LinkFactory.createInstance(v , VertexInitTypes.DEAD_END_VERTEX, LinkType.METHOD_CALL));
        }
    }
}