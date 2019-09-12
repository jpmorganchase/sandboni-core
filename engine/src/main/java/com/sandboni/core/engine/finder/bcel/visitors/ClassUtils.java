package com.sandboni.core.engine.finder.bcel.visitors;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.util.SyntheticRepository;

import java.util.*;
import java.util.stream.Collectors;

class ClassUtils {

    private ClassUtils() {
    }

    private static final SyntheticRepository repository = SyntheticRepository.getInstance();

    static List<JavaClass> getInScopeInterfacesSafe(JavaClass jc) {
        List<JavaClass> result = new ArrayList<>();

        for (String in : jc.getInterfaceNames()) {
            try {
                JavaClass inClass = repository.loadClass(in);
                result.addAll(getInScopeInterfacesSafe(inClass));
                result.add(inClass);
            } catch (Exception ignored) {
                //need to look for  approach - we cannot continue spitting out logs
            }
        }
        return result;
    }

    static Map<Method, JavaClass> getInheritedMethods(JavaClass jc) {
        Map<Method, JavaClass> result = new HashMap<>();
        Set<Method> existingMethods = new HashSet<>(getInstanceMethods(jc));

        for (JavaClass superClass : getSuperClassesSafe(jc)) {
            for (Method method : superClass.getMethods()) {
                if (!method.isAbstract() && !method.isStatic() && !existingMethods.contains(method) && !result.containsKey(method)) {
                    result.put(method, superClass);
                }
                existingMethods.add(method);
            }
        }
        return result;
    }

    private static List<Method> getInstanceMethods(JavaClass javaClass) {
        return Arrays.stream(javaClass.getMethods()).filter(m -> !m.isStatic() && !m.getName().contains("<")).collect(Collectors.toList());
    }

    private static List<JavaClass> getSuperClassesSafe(JavaClass jc) {
        List<JavaClass> result = new ArrayList<>();
        JavaClass clazz;
        try {
            for (clazz = jc.getSuperClass(); clazz != null; clazz = clazz.getSuperClass()) {
                result.add(clazz);
            }
        } catch (Exception ignored) {
            //need to look for  approach - we cannot continue spitting out logs
        }
        return result;
    }

    static List<AbstractMap.SimpleEntry<Method, JavaClass>> getOverriddenMethods(JavaClass jc) {
        List<AbstractMap.SimpleEntry<Method, JavaClass>> result = new ArrayList<>();

        Set<Method> existingMethods = new HashSet<>(getInstanceMethods(jc));

        List<JavaClass> superClassesTopToBottom = getSuperClassesSafe(jc);
        Collections.reverse(superClassesTopToBottom);

        Map<JavaClass, List<Method>> allSuperMethods = superClassesTopToBottom
                .stream().collect(Collectors.toMap(c -> c, ClassUtils::getInstanceMethods));

        for (Method method : existingMethods) {
            for (int i = 0; i < superClassesTopToBottom.size(); i++) {
                if (allSuperMethods.get(superClassesTopToBottom.get(i)).contains(method)) {
                    for (int j = i; j < superClassesTopToBottom.size(); j++) {
                        result.add(new AbstractMap.SimpleEntry<>(method, superClassesTopToBottom.get(i)));
                    }
                    break;
                }
            }
        }
        return result;
    }

    static Map<Method, JavaClass> getInheritedInterfaceMethods(JavaClass iface) {
        Map<Method, JavaClass> result = new HashMap<>();
        Set<Method> existingMethods = new HashSet<>(ClassUtils.getInstanceMethods(iface));

        for (JavaClass superClass : getInScopeInterfacesSafe(iface)) {
            for (Method method : superClass.getMethods()) {
                if (!existingMethods.contains(method)) {
                    result.put(method, superClass);
                }
            }
        }
        return result;
    }
}