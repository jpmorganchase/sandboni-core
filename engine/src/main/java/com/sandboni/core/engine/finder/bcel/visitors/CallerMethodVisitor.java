package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.SyntheticRepository;

import java.util.Arrays;
import java.util.Optional;

import static com.sandboni.core.engine.common.StreamHelper.ofType;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.*;

public class CallerMethodVisitor extends CallerFieldOrMethodVisitor {
    private static final String OPTIONS_PACKAGE = "Lorg/fusesource/restygwt/client/Options;";

    CallerMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
        addLink(LinkFactory.createInstance(
                context.getApplicationId(),
                currentMethodVertex,
                new Vertex.Builder(i.getReferenceType(cp).toString(),
                        formatMethod(i.getMethodName(cp), i.getArgumentTypes(cp))).build(),
                LinkType.METHOD_CALL));
    }

    @Override
    public void visitNEW(NEW obj) {
        addLink(LinkFactory.createInstance(
                context.getApplicationId(),
                currentMethodVertex,
                new Vertex.Builder(obj.getLoadClassType(cp).getClassName(), MethodUtils.INIT).build(),
                LinkType.SPECIAL_CALL));
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
        Vertex v = new Vertex.Builder(i.getReferenceType(cp).toString(),
                formatMethod(i.getMethodName(cp),
                        i.getArgumentTypes(cp)),
                context.getCurrentLocation())
                .build();
        addLink(LinkFactory.createInstance(
                context.getApplicationId(),
                currentMethodVertex, v, LinkType.INTERFACE_CALL));
        if (isInvokerInterfaceController(i.getClassName(cp))) {
            addLink(LinkFactory.createInstance(context.getApplicationId(), v, currentMethodVertex, LinkType.INTERFACE_CALL));
        }
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
        addLink(LinkFactory.createInstance(
                context.getApplicationId(), currentMethodVertex,
                new Vertex.Builder(i.getReferenceType(cp).toString(), formatMethod(i.getMethodName(cp), i.getArgumentTypes(cp))).build(),
                LinkType.SPECIAL_CALL));
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
        addLink(LinkFactory.createInstance(
                context.getApplicationId(), currentMethodVertex,
                new Vertex.Builder(i.getReferenceType(cp).toString(), formatMethod(i.getMethodName(cp), i.getArgumentTypes(cp))).build(),
                LinkType.STATIC_CALL));
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
        //support for lambdas via link to bootstrap method
        ConstantInvokeDynamic constantInvokeDynamic = (ConstantInvokeDynamic) cp.getConstant(i.getIndex());

        Optional<BootstrapMethods> bootstrapMethods = Arrays.stream(this.javaClass.getAttributes()).flatMap(ofType(BootstrapMethods.class)).findFirst();
        if (bootstrapMethods.isPresent()) {

            BootstrapMethod bootstrapMethod = bootstrapMethods.get().getBootstrapMethods()[constantInvokeDynamic.getBootstrapMethodAttrIndex()];
            int[] bootstrapMethodArguments = bootstrapMethod.getBootstrapArguments();
            for (int a : bootstrapMethodArguments) {
                if (cp.getConstant(a) instanceof ConstantMethodHandle) {
                    ConstantMethodHandle cmh = (ConstantMethodHandle) cp.getConstant(a);

                    ConstantCP ccp = (ConstantCP) cp.getConstant(cmh.getReferenceIndex());
                    String typeName = ccp.getClass(cp.getConstantPool());

                    ConstantNameAndType cnt = (ConstantNameAndType) cp.getConstant(ccp.getNameAndTypeIndex());
                    String methodName = getMethodNameAndType(cp, cnt);

                    // trimming return type and remove space
                    methodName = formatMethodName(methodName);
                    addLink(LinkFactory.createInstance(
                            context.getApplicationId(), currentMethodVertex,
                            new Vertex.Builder(typeName, methodName).build(),
                            LinkType.DYNAMIC_CALL));
                }
            }
        }
    }



    private boolean isInvokerInterfaceController(String className) {
        try {
            SyntheticRepository repository = ClassUtils.getRepository(context.getClassPath());
            JavaClass clazz = repository.loadClass(className);
            return clazz.isInterface() && Arrays.stream(clazz.getAnnotationEntries()).anyMatch(a -> a.getAnnotationType().equals(OPTIONS_PACKAGE));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
