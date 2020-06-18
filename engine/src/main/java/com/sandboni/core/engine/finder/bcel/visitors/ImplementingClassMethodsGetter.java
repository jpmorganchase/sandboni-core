package com.sandboni.core.engine.finder.bcel.visitors;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImplementingClassMethodsGetter {
    static List<Method> getImplementingClassMethods(JavaClass implementingClass) {
        return Arrays.stream(implementingClass.getMethods()).filter(m -> !m.isStatic() && !m.isAbstract()).collect(Collectors.toList());
    }
}
