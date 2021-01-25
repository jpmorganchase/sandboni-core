package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.sta.Context;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotation;
import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotationParameter;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.finder.bcel.visitors.http.HttpLinkHelper.addHttpLinks;

public class AnnotationValueHandler {

    /**
     * Handles all path annotation logic
     * @param javaClass the calss
     * @param method the method
     * @param parentPath the parent/class path's value
     * @param context the context
     * @param controllerSide is it the controller handler side
     */
    public static void handlePathAnnotation(JavaClass javaClass, Method method, String parentPath, Context context, boolean controllerSide) {
        //checking @Path - handle multiple urls
        AnnotationEntry path = getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, Annotations.JAVAX.PATH.getDesc());
        if (path != null) {
            String value = getAnnotationParameter(path, "value");  //might contains multiple urls divided by '|': '/abc,/fgh,/ijk/'

            Arrays.stream(value.split(",")).forEach(s -> {
                String methodPath = parentPath + s;
                Set<String> requestMethods = HttpConsts.getHttpVerb().stream()
                        .filter(m -> getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, "/" + m + ";") != null)
                        .collect(Collectors.toSet());

                addHttpLinks(requestMethods, context, methodPath, javaClass.getClassName(), formatMethod(method), controllerSide);
            });
        }
    }

    private AnnotationValueHandler() {
        // static methods
    }
}
