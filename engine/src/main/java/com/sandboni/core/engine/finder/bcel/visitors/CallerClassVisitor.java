package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.timing.SWConsts;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
import org.apache.bcel.classfile.Method;

import java.util.Arrays;
import java.util.Objects;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.DEAD_END_VERTEX;

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
        StopWatch sw1 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_VISIT_METHOD, "linksAdded (CallerFieldOrMethodVisitor)").start();
        long linksAdded = Arrays.stream(getVisitors(method))
                .map(CallerFieldOrMethodVisitor::start)
                .mapToInt(i -> i).sum();
        sw1.stop();

        // in order to detect locations (modules) each method has to have at least  one entry in the graph as caller
        if (linksAdded == 0) {
            Vertex v;
            if (Objects.nonNull(context.getCurrentLocation()) && context.getTestLocations().contains(context.getCurrentLocation())) {
                StopWatch sw2 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_VISIT_METHOD, "create TestVertex").start();
                v = new TestVertex.Builder(this.javaClass.getClassName(), formatMethod(method), context.getCurrentLocation()).build();
                sw2.stop();
            } else {
                StopWatch sw3 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_VISIT_METHOD, "create Vertex").start();
                v = new Vertex.Builder(this.javaClass.getClassName(), formatMethod(method), context.getCurrentLocation()).build();
                sw3.stop();
            }
            StopWatch sw4 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_VISIT_METHOD, "addLink").start();
            context.addLink(LinkFactory.createInstance(context.getApplicationId(), v, DEAD_END_VERTEX, LinkType.METHOD_CALL));
            sw4.stop();
        }
    }
}