package com.sandboni.core.engine.finder.bcel.visitors.http;

import com.sandboni.core.engine.finder.bcel.visitors.MethodVisitorBase;
import com.sandboni.core.engine.sta.Context;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationValueHandler.handlePathAnnotation;

public class JavaxControllerMethodVisitor extends MethodVisitorBase {

    private final String parentPath;
    private boolean controllerSide;

    public JavaxControllerMethodVisitor(Method m, JavaClass jc, Context c, String parentPath, boolean controllerSide) {
        super(m, jc, c);
        this.parentPath = parentPath == null ? "" : parentPath;
        this.controllerSide = controllerSide;
    }

    public void start() {
        handlePathAnnotation(javaClass, method, parentPath, context, controllerSide);
    }
}