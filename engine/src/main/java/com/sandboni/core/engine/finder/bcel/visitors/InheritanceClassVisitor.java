package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import static com.sandboni.core.engine.finder.bcel.visitors.ClassUtils.*;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.getRelativeFileName;

/**
 * Visit Java classes that inherit, override or implement a method.
 * Note: This class is not thread safe.
 */
public class InheritanceClassVisitor extends ClassVisitorBase implements ClassVisitor {

    @Override
    public void visitJavaClass(JavaClass jc) {
        if (jc.isClass()) {
            getInheritedMethods(jc).forEach((key, value) -> addLink(jc, key, value, LinkType.FORWARD_TO));

            getOverriddenMethods(jc).forEach(e ->  addLink(e.getValue(), e.getKey(), jc, LinkType.OVERRIDDEN));
        }
        if (jc.isInterface()) {
            getInheritedInterfaceMethods(jc).forEach((key, value) -> addLink(jc, key, value, LinkType.FORWARD_TO));
        }
    }

    private void addLink(JavaClass jc, Method key, JavaClass value, LinkType forwardTo) {
        String methodName = MethodUtils.formatMethod(key.getName(), key.getArgumentTypes());
        context.addLink(LinkFactory.createInstance(
                new Vertex.Builder(jc.getClassName(), methodName, context.getCurrentLocation()).build(),
                new Vertex.Builder(value.getClassName(), methodName)
                        .withFilePath(getRelativeFileName(value))
                        .withLineNumbers(MethodUtils.getMethodLineNumbers(key))
                        .build(),
                forwardTo));
    }


}