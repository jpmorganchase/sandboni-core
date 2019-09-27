package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Objects;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotation;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.finder.bcel.visitors.TestClassVisitor.JUNIT_PACKAGE;
import static com.sandboni.core.engine.finder.bcel.visitors.TestClassVisitor.TESTING_PACKAGE;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;

public class TestMethodVisitor extends MethodVisitorBase {
    final boolean testMethod;

    private boolean ignore;

    TestMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
        testMethod = getAnnotation(jc.getConstantPool(), m::getAnnotationEntries, JUNIT_PACKAGE, TESTING_PACKAGE) != null;
    }

    TestMethodVisitor(Method m, JavaClass jc, Context c, boolean ignore) {
        super(m, jc, c);
        testMethod = getAnnotation(jc.getConstantPool(), m::getAnnotationEntries, JUNIT_PACKAGE, TESTING_PACKAGE) != null;
        this.ignore = testMethod &&
                (ignore || Objects.nonNull(getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, Annotations.TEST.IGNORE.getDesc())));
    }

    public void start() {
        if (testMethod) {
            String methodName = formatMethod(method);
            TestVertex tv = new TestVertex.Builder(javaClass.getClassName(), methodName, context.getCurrentLocation())
                    .withIgnore(ignore)
                    .build();
            context.addLink(LinkFactory.createInstance(context.getApplicationId(), START_VERTEX, tv, LinkType.ENTRY_POINT));
        }
    }
}