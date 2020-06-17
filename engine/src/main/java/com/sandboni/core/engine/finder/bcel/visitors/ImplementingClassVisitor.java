package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.finder.bcel.visitors.ClassUtils.getInScopeInterfacesSafe;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.*;

/**
 * Visit Java classes that implement an interface.
 * Note: This class is not thread safe.
 */
public class ImplementingClassVisitor extends ClassVisitorBase implements ClassVisitor {

    private static class InterfaceVisitor extends EmptyVisitor {

        private final List<Method> implementingClassMethods;
        private JavaClass implementingClass;
        private Context context;
        private JavaClass javaClass;

        InterfaceVisitor(JavaClass implementingClass, Context context) {
            this.implementingClass = implementingClass;
            this.context = context;
            this.implementingClassMethods = getImplementingClassMethods(implementingClass);
        }

        @Override
        public void visitJavaClass(JavaClass jc) {
            this.javaClass = jc;
            for (Method method : jc.getMethods()) {
                    method.accept(this);
            }
        }

        @Override
        public void visitMethod(Method method) {
            if (implementingClassMethods.contains(method)) {
                int methodIndex = implementingClassMethods.indexOf(method);
                Method methodImplementor = implementingClassMethods.get(methodIndex);
                if ((methodImplementor.getAccessFlags() & Const.ACC_BRIDGE) != 0 && methodImplementor.isSynthetic()) {
                    BridgeMethodVisitor bridgeMethodVisitor = new BridgeMethodVisitor(methodImplementor, javaClass, implementingClass, context);
                    bridgeMethodVisitor.start();
                } else {
                    String methodName = formatMethod(method);
                    context.addLink(LinkFactory.createInstance(
                            context.getApplicationId(),
                            new Vertex.Builder(javaClass.getClassName(), methodName, context.getCurrentLocation()).build(),
                            new Vertex.Builder(implementingClass.getClassName(), methodName)
                                    .withFilePath(getRelativeFileName(implementingClass))
                                    .withLineNumbers(MethodUtils.getMethodLineNumbers(method))
                                    .build(),
                            LinkType.INTERFACE_IMPL));
                }
            }
        }

        private void start(JavaClass jc) {
            visitJavaClass(jc);
        }
    }

    static List<Method> getImplementingClassMethods(JavaClass implementingClass) {
        return Arrays.stream(implementingClass.getMethods()).filter(m -> !m.isStatic() && !m.isAbstract()).collect(Collectors.toList());
    }

    @Override
    public void visitJavaClass(JavaClass jc) {
        InterfaceVisitor visitor = new InterfaceVisitor(jc, context);
        getInScopeInterfacesSafe(jc, context.getClassPath()).forEach(visitor::start);
    }
}
