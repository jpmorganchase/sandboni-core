package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.INVOKEVIRTUAL;

import java.util.List;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.getMethodNameAndType;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.getRelativeFileName;

public class BridgeMethodVisitor extends MethodVisitorBase {

    private final JavaClass implementingClass;
    private final List<Method> classMethods;

    BridgeMethodVisitor(Method m, JavaClass jc, JavaClass implementingClass, Context c) {
        super(m, jc, c);
        this.implementingClass = implementingClass;
        classMethods = ImplementingClassMethodsGetter.getImplementingClassMethods(implementingClass);
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL invoke) {
        ConstantPool cp = method.getConstantPool();
        ConstantMethodref methodRef = (ConstantMethodref) cp.getConstant(invoke.getIndex());
        ConstantNameAndType methodNameType = (ConstantNameAndType) cp.getConstant(methodRef.getNameAndTypeIndex());
        String methodSignature = MethodUtils.formatMethodName(getMethodNameAndType(cp, methodNameType));

        classMethods.stream()
                .map(MethodUtils::formatMethod)
                .filter(methodSignature::equals)
                .findAny()
                .ifPresent(methodName -> {
                            context.addLink(LinkFactory.createInstance(
                                    context.getApplicationId(),
                                    new Vertex.Builder(javaClass.getClassName(), methodSignature, context.getCurrentLocation()).build(),
                                    new Vertex.Builder(implementingClass.getClassName(), methodSignature)
                                            .withFilePath(getRelativeFileName(implementingClass))
                                            .withLineNumbers(MethodUtils.getMethodLineNumbers(method))
                                            .build(),
                                    LinkType.INVOKE_VIRTUAL));
                            context.addLink(LinkFactory.createInstance(
                                    context.getApplicationId(),
                                    new Vertex.Builder(javaClass.getClassName(), MethodUtils.formatMethod(method))
                                            .withFilePath(getRelativeFileName(implementingClass))
                                            .withLineNumbers(MethodUtils.getMethodLineNumbers(method))
                                            .build(),
                                    new Vertex.Builder(javaClass.getClassName(), methodSignature)
                                            .withFilePath(getRelativeFileName(implementingClass))
                                            .withLineNumbers(MethodUtils.getMethodLineNumbers(method))
                                            .build(),
                                    LinkType.INVOKE_VIRTUAL));
                        }
                );
    }

    void start() {
        visitInstructions(method);
    }
}
