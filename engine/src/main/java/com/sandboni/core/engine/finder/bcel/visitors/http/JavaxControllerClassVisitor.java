package com.sandboni.core.engine.finder.bcel.visitors.http;

import com.sandboni.core.engine.finder.bcel.visitors.ClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.Annotations;
import com.sandboni.core.engine.finder.bcel.visitors.ClassVisitorBase;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.stream.Stream;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotation;
import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotationParameter;

/**
 * Visit Java classes that uses Javax.Path annotation.
 * Note: This class is not thread safe.
 */
public class JavaxControllerClassVisitor extends ClassVisitorBase implements ClassVisitor {

    private String controllerPath;

    @Override
    public void visitMethod(Method method) {
        new JavaxControllerMethodVisitor(method, javaClass, context, controllerPath, controllerPath != null ).start();
    }

    @Override
    public Stream<Link> start(JavaClass jc, Context c) {
        controllerPath = null;
        AnnotationEntry path = getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.JAVAX.PATH.getDesc(), Annotations.JAVAX.OPTIONS.getDesc());

        if (path != null) {
            controllerPath = path.getAnnotationType().contains(Annotations.JAVAX.PATH.getDesc()) ?
                    getAnnotationParameter(path, "value") : "";
        }
        return super.start(jc, c);
    }
}
