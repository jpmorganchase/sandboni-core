package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotation;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.getRelativeFileName;
import static com.sandboni.core.engine.finder.bcel.visitors.TestClassVisitor.ALL_TEST_PACKAGES;

abstract class CallerFieldOrMethodVisitor extends MethodVisitorBase {
    final Vertex currentMethodVertex;
    final ConstantPoolGen cp;

    CallerFieldOrMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
        cp = new ConstantPoolGen(jc.getConstantPool());

        boolean testMethod = getAnnotation(jc.getConstantPool(), m::getAnnotationEntries, ALL_TEST_PACKAGES).isPresent();

        boolean ignore = AnnotationUtils.isIgnore(jc, jc::getAnnotationEntries);

        if (!ignore)
            ignore = AnnotationUtils.isIgnore(jc, m::getAnnotationEntries);

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
