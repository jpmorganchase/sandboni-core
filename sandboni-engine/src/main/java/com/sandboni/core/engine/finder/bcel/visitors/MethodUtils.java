package com.sandboni.core.engine.finder.bcel.visitors;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.Method;
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
}
