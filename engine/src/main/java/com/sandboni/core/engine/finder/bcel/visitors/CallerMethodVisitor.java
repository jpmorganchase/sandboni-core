package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.common.StreamHelper;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.util.Arrays;
import java.util.Optional;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;

public class CallerMethodVisitor extends CallerFieldOrMethodVisitor {
    private static final String OPTIONS_PACKAGE = "Lorg/fusesource/restygwt/client/OPTIONS;";

    CallerMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
        addLink(LinkFactory.createInstance(
                currentMethodVertex,
                new Vertex.Builder(i.getReferenceType(cp).toString(),
                        MethodUtils.formatMethod(i.getMethodName(cp), i.getArgumentTypes(cp))).build(),
                LinkType.METHOD_CALL));
    }

    @Override
    public void visitNEW(NEW obj) {
        addLink(LinkFactory.createInstance(currentMethodVertex,
                new Vertex.Builder(obj.getLoadClassType(cp).getClassName(), MethodUtils.INIT).build(),
                LinkType.SPECIAL_CALL));
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
        Vertex v = new Vertex.Builder(i.getReferenceType(cp).toString(),
                MethodUtils.formatMethod(i.getMethodName(cp),
                        i.getArgumentTypes(cp)),
                context.getCurrentLocation())
                .build();
        addLink(LinkFactory.createInstance(currentMethodVertex, v, LinkType.INTERFACE_CALL));
        if (isInvokerInterfaceController(i.getClassName(cp))) {
            addLink(LinkFactory.createInstance(v, currentMethodVertex, LinkType.INTERFACE_CALL));
        }
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
        addLink(LinkFactory.createInstance(currentMethodVertex,
                new Vertex.Builder(i.getReferenceType(cp).toString(), MethodUtils.formatMethod(i.getMethodName(cp), i.getArgumentTypes(cp))).build(),
                LinkType.SPECIAL_CALL));
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
        addLink(LinkFactory.createInstance(currentMethodVertex,
                new Vertex.Builder(i.getReferenceType(cp).toString(), MethodUtils.formatMethod(i.getMethodName(cp), i.getArgumentTypes(cp))).build(),
                LinkType.STATIC_CALL));
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
        //support for lambdas via link to bootstrap method
        ConstantInvokeDynamic constantInvokeDynamic = (ConstantInvokeDynamic) cp.getConstant(i.getIndex());

        Optional<BootstrapMethods> bootstrapMethods = Arrays.stream(this.javaClass.getAttributes()).flatMap(StreamHelper.ofType(BootstrapMethods.class)).findFirst();
        if (bootstrapMethods.isPresent()) {
            int[] bootstrapMethodArguments = bootstrapMethods.get().getBootstrapMethods()[constantInvokeDynamic.getBootstrapMethodAttrIndex()].getBootstrapArguments();
            for (int a : bootstrapMethodArguments) {
                if (cp.getConstant(a) instanceof ConstantMethodHandle) {
                    ConstantMethodHandle cmh = (ConstantMethodHandle) cp.getConstant(a);

                    ConstantCP ccp = (ConstantCP) cp.getConstant(cmh.getReferenceIndex());
                    String typeName = ccp.getClass(cp.getConstantPool());

                    ConstantNameAndType cnt = (ConstantNameAndType) cp.getConstant(ccp.getNameAndTypeIndex());
                    String methodName = Utility.methodSignatureToString(cnt.getSignature(cp.getConstantPool()), cnt.getName(cp.getConstantPool()), "", false, new LocalVariableTable(0, 0, new LocalVariable[]{}, cp.getConstantPool()));

                    // trimming return type and remove space
                    methodName = methodName.substring(methodName.indexOf(' ') + 1).replace(" ", "");
                    addLink(LinkFactory.createInstance(currentMethodVertex,
                            new Vertex.Builder(typeName, methodName).build(),
                            LinkType.DYNAMIC_CALL));
                }
            }
        }
    }

    private boolean isInvokerInterfaceController(String className) {
        try {
            JavaClass clazz = Repository.lookupClass(className);
            return clazz.isInterface() && Arrays.stream(clazz.getAnnotationEntries()).anyMatch(a -> a.getAnnotationType().equals(OPTIONS_PACKAGE));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
