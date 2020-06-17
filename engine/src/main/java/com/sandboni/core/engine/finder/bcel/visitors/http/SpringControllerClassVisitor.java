package com.sandboni.core.engine.finder.bcel.visitors.http;

import com.sandboni.core.engine.finder.bcel.visitors.ClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils;
import com.sandboni.core.engine.finder.bcel.visitors.Annotations;
import com.sandboni.core.engine.finder.bcel.visitors.ClassVisitorBase;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.stream.Stream;

/**
 * Visit Java classes that uses Spring.Controller annotations.
 * Note: This class is not thread safe.
 */
public class SpringControllerClassVisitor extends ClassVisitorBase implements ClassVisitor {

    @Override
    public void visitMethod(Method method) {
        new SpringControllerMethodVisitor(method, javaClass, context, true).start();
    }

    @Override
    public Stream<Link> start(JavaClass jc, Context c) {
        if (AnnotationUtils.getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.SPRING.CONTROLLER.getDesc()) != null) {
            return super.start(jc, c);
        }
        return Stream.empty();
    }
}