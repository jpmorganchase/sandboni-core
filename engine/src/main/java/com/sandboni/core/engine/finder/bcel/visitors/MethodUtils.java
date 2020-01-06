package com.sandboni.core.engine.finder.bcel.visitors;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodUtils {

    public static final String CLINIT = "<clinit>()";
    public static final String INIT = "<init>()";

    private MethodUtils() {
    }

    static String getRelativeFileName(JavaClass jc) {
        //in case no package name supplied in file
        if (jc.getPackageName().isEmpty()){
            return jc.getSourceFileName();
        }
        return jc.getPackageName().replace(".", "/") + "/" + jc.getSourceFileName();
    }

    public static String formatMethod(Method method) {
        return formatMethod(method.getName(), method.getArgumentTypes());
    }

    public static List<Integer> getMethodLineNumbers(Method method) {
        if (method != null && method.getLineNumberTable() != null && method.getLineNumberTable().getLineNumberTable() != null) {
            return Arrays.stream(method.getLineNumberTable().getLineNumberTable()).map(LineNumber::getLineNumber).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    protected static String formatMethod(String methodName, Type[] argumentTypes) {
        return String.format("%s(%s)", methodName, argumentList(argumentTypes));
    }

    private static String argumentList(Type[] arguments) {
        return Arrays.stream(arguments)
                .map(Type::toString)
                .collect(Collectors.joining(","));
    }

    public static String getMethodNameAndType(ConstantPoolGen cp, ConstantNameAndType methodNameType) {
        return getMethodNameAndType(cp.getConstantPool(), methodNameType);
    }

    public static String getMethodNameAndType(ConstantPool cp, ConstantNameAndType methodNameType) {
        return Utility.methodSignatureToString(methodNameType.getSignature(cp), methodNameType.getName(cp),
                "", false, new LocalVariableTable(0, 0, new LocalVariable[]{}, cp));
    }

    public static String formatMethodName(String methodName) {
        return methodName.substring(methodName.indexOf(' ') + 1).replace(" ", "");
    }
}
