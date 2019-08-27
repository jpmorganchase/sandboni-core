package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;

import java.util.Objects;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotation;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.getRelativeFileName;
import static com.sandboni.core.engine.finder.bcel.visitors.TestClassVisitor.JUNIT_PACKAGE;
import static com.sandboni.core.engine.finder.bcel.visitors.TestClassVisitor.TESTING_PACKAGE;

abstract class CallerFieldOrMethodVisitor extends MethodVisitorBase {
    final Vertex currentMethodVertex;
    final ConstantPoolGen cp;

    CallerFieldOrMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
        cp = new ConstantPoolGen(jc.getConstantPool());
        boolean testMethod = getAnnotation(jc.getConstantPool(), m::getAnnotationEntries, JUNIT_PACKAGE, TESTING_PACKAGE) != null;

        boolean ignore = Objects.nonNull(AnnotationUtils.getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.TEST.IGNORE.getDesc()));
        if (!ignore)
            ignore = Objects.nonNull(AnnotationUtils.getAnnotation(jc.getConstantPool(), m::getAnnotationEntries, Annotations.TEST.IGNORE.getDesc()));

        if (testMethod){
            currentMethodVertex = new TestVertex.Builder(jc.getClassName(), formatMethod(m.getName(), m.getArgumentTypes()), context.getCurrentLocation())
                    .withFilePath(getRelativeFileName(jc))
                    .withLineNumbers(MethodUtils.getMethodLineNumbers(m))
                    .withIgnore(ignore)
                    .build();
        }else {
            currentMethodVertex = new Vertex.Builder(jc.getClassName(), formatMethod(m.getName(), m.getArgumentTypes()), context.getCurrentLocation())
                    .withFilePath(getRelativeFileName(jc))
                    .withLineNumbers(MethodUtils.getMethodLineNumbers(m))
                    .build();
        }
    }

    int start() {
        if (method.isAbstract() || method.isNative()) {
            return 0;
        }
        visitInstructions(method);
        return linksCount;
    }
}
