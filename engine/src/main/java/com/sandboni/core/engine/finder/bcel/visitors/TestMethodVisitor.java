package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Objects;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;

public class TestMethodVisitor extends MethodVisitorBase {
    final boolean testMethod;

    private boolean ignore;

    TestMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
        testMethod = AnnotationUtils.getAnnotation(jc.getConstantPool(), m::getAnnotationEntries, TestClassVisitor.JUNIT_PACKAGE, TestClassVisitor.TESTING_PACKAGE) != null;
    }

    TestMethodVisitor(Method m, JavaClass jc, Context c, boolean ignore) {
        super(m, jc, c);
        testMethod = AnnotationUtils.getAnnotation(jc.getConstantPool(), m::getAnnotationEntries, TestClassVisitor.JUNIT_PACKAGE, TestClassVisitor.TESTING_PACKAGE) != null;
        this.ignore = testMethod &&
                (ignore || Objects.nonNull(AnnotationUtils.getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, Annotations.TEST.IGNORE.getDesc())));
    }

    public void start() {
        if (testMethod) {
            String methodName = MethodUtils.formatMethod(method);
            TestVertex tv = new TestVertex.Builder(javaClass.getClassName(), methodName, context.getCurrentLocation())
                    .withIgnore(ignore)
                    .build();
            context.addLink(LinkFactory.createInstance(VertexInitTypes.START_VERTEX, tv, LinkType.ENTRY_POINT));
        }
    }
}