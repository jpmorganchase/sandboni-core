package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.util.Arrays;
import java.util.Optional;

import static com.sandboni.core.engine.common.StreamHelper.ofType;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;

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
            String interfaceClassName = null;
            String interfaceMethodName = null;
            if (context.isEnableExperimental() && cp.getConstant(((ConstantMethodHandle) cp.getConstant(bootstrapMethod.getBootstrapMethodRef())).getReferenceKind()) instanceof ConstantInterfaceMethodref) {
                // get the original name of the interface and method to be implemented by lambda
                ConstantInterfaceMethodref interfaceMethodRef = (ConstantInterfaceMethodref) cp.getConstant(((ConstantMethodHandle) cp.getConstant(bootstrapMethod.getBootstrapMethodRef())).getReferenceKind());
                interfaceClassName = ((ConstantUtf8) cp.getConstant(((ConstantClass) cp.getConstant(interfaceMethodRef.getClassIndex())).getNameIndex()))
                        .getBytes().replace('/', '.');
                ConstantNameAndType methodNameType = (ConstantNameAndType) cp.getConstant(interfaceMethodRef.getNameAndTypeIndex());
                interfaceMethodName = formatMethodName(getMethodNameAndType(methodNameType));
            }

            int[] bootstrapMethodArguments = bootstrapMethod.getBootstrapArguments();
            for (int a : bootstrapMethodArguments) {
                if (cp.getConstant(a) instanceof ConstantMethodHandle) {
                    ConstantMethodHandle cmh = (ConstantMethodHandle) cp.getConstant(a);

                    ConstantCP ccp = (ConstantCP) cp.getConstant(cmh.getReferenceIndex());
                    String typeName = ccp.getClass(cp.getConstantPool());

                    ConstantNameAndType cnt = (ConstantNameAndType) cp.getConstant(ccp.getNameAndTypeIndex());
                    String methodName = getMethodNameAndType(cnt);

                    // trimming return type and remove space
                    methodName = formatMethodName(methodName);
                    addLink(LinkFactory.createInstance(
                            context.getApplicationId(), currentMethodVertex,
                            new Vertex.Builder(typeName, methodName).build(),
                            LinkType.DYNAMIC_CALL));

                    if (context.isEnableExperimental() && interfaceClassName != null && interfaceMethodName != null) {
                        addLink(LinkFactory.createInstance(
                                context.getApplicationId(),
                                new Vertex.Builder(interfaceClassName, interfaceMethodName).build(),
                                new Vertex.Builder(typeName, methodName).build(),
                                LinkType.DYNAMIC_CALL));
                    }
                }
            }
        }
    }

    private String getMethodNameAndType(ConstantNameAndType methodNameType) {
        return Utility.methodSignatureToString(methodNameType.getSignature(cp.getConstantPool()), methodNameType.getName(cp.getConstantPool()),
                "", false, new LocalVariableTable(0, 0, new LocalVariable[]{}, cp.getConstantPool()));
    }

    private String formatMethodName(String methodName) {
        return methodName.substring(methodName.indexOf(' ') + 1).replace(" ", "");
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
