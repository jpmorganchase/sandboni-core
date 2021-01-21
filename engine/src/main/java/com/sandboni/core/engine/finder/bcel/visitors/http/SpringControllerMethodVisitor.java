package com.sandboni.core.engine.finder.bcel.visitors.http;

import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils;
import com.sandboni.core.engine.finder.bcel.visitors.Annotations;
import com.sandboni.core.engine.finder.bcel.visitors.MethodVisitorBase;
import com.sandboni.core.engine.finder.bcel.visitors.RedirectVisitor;
import com.sandboni.core.engine.sta.Context;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotation;
import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotationParameter;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.finder.bcel.visitors.http.HttpLinkHelper.addHttpLinks;
import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.SpringAnnotations.*;


public class SpringControllerMethodVisitor extends MethodVisitorBase {

    private boolean controllerSide;

    public SpringControllerMethodVisitor(Method m, JavaClass jc, Context c, boolean controllerSide) {
        super(m, jc, c);
        this.controllerSide = controllerSide;
    }

    public void start() {
        Optional<AnnotationEntry> requestMapping = getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, getAvailableRequestMappingAnnotations());

        if (requestMapping.isPresent()) {
            String valueAnnotation = getAnnotationParameter(requestMapping.get(), "value", "path");
            if (valueAnnotation != null) {
                String[] requestUrls = valueAnnotation.split(",");
                for (String requestUrl : requestUrls) {
                    String requestMethod = getRequestMethod(requestMapping.get(), javaClass.getConstantPool());
                    Set<String> requestMethods;
                    if (!requestMethod.isEmpty()){
                        requestMethods  = new HashSet<>(Collections.singletonList(requestMethod));
                    }else{
                        requestMethods = HttpConsts.getHttpVerb();
                    }
                    addHttpLinks(requestMethods, context, requestUrl, javaClass.getClassName(), formatMethod(method), controllerSide);
                }
                new RedirectVisitor(method, javaClass, context, false).start();
            }
        }
    }

    /**
     * Return the required Http method for the given annotation
     */
    private String getRequestMethod(AnnotationEntry requestMapping, ConstantPool constantPool) {
        String requestMethod;
        String signature = AnnotationUtils.getTypeSignature(constantPool, requestMapping.getTypeIndex());

        if (signature.contains(Annotations.SPRING.REQUEST_MAPPING.getDesc())) {
            requestMethod = getAnnotationParameter(requestMapping, "method");
        } else{
            int beginIndex = (signature.lastIndexOf('/') >= 0 ? signature.lastIndexOf('/') : signature.lastIndexOf('\\')) + 1;
            requestMethod = signature.substring(beginIndex, signature.lastIndexOf("Mapping")).toUpperCase();
        }

        return requestMethod;
    }
}