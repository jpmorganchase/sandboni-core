package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.http.JavaxControllerMethodVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.http.SpringControllerMethodVisitor;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.*;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotation;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;

/**
 * Visit Java Test classes.
 * Note: This class is not thread safe.
 */
public class TestClassVisitor extends ClassVisitorBase implements ClassVisitor {
    static final String JUNIT_PACKAGE = "org/junit/Test";
    static final String TESTING_PACKAGE = "org/testing/annotations/Test";

    private Set<String> testMethods = new HashSet<>();
    private Map<String, LinkType> initMethods = new HashMap<>();

    private boolean ignore;
    private boolean classIncluded;

    public void setUp() {
        ignore = false;
        testMethods = new HashSet<>();
        initMethods = new HashMap<>();
        initMethods.put(MethodUtils.INIT, LinkType.METHOD_CALL);
        initMethods.put(MethodUtils.CLINIT, LinkType.STATIC_CALL);
    }

    @Override
    public void visitMethod(Method method) {
        boolean testMethod = getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, JUNIT_PACKAGE, TESTING_PACKAGE) != null;
        if (testMethod) {
            new TestMethodVisitor(method, javaClass, context, ignore, classIncluded).start();
            new TestHttpMethodVisitor(method, javaClass, context).start();
            new SpringControllerMethodVisitor(method, javaClass, context, false).start();
            new JavaxControllerMethodVisitor(method, javaClass, context, null, false).start();
            new SpringMockMvcMethodVisitor(method, javaClass, context).start();
            testMethods.add(formatMethod(method));
        }
        boolean initMethod = getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, Annotations.TEST.BEFORE.getDesc()) != null ||
                getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, Annotations.TEST.AFTER.getDesc()) != null;
        if (initMethod) {
            initMethods.put(formatMethod(method), method.isStatic() ? LinkType.STATIC_CALL : LinkType.METHOD_CALL);
        }
    }

    @Override
    public synchronized void visitJavaClass(JavaClass jc) {
        setUp();
        this.ignore = Objects.nonNull(AnnotationUtils.getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.TEST.IGNORE.getDesc()));
        this.classIncluded = Objects.nonNull(AnnotationUtils.getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, context.getIncludeTestAnnotation()));
        super.visitJavaClass(jc);

        testMethods.stream()
                .flatMap(tm -> initMethods.entrySet().stream().map(es -> new Object() {
                    String testMethod = tm;
                    String initMethod = es.getKey();
                    LinkType linkType = es.getValue();
                }))
                .map(pl -> LinkFactory.createInstance(context.getApplicationId(), new TestVertex.Builder(jc.getClassName(), pl.testMethod, context.getCurrentLocation()).build(),
                        new Vertex.Builder(jc.getClassName(), pl.initMethod,context.getCurrentLocation()).build(), pl.linkType))
                .forEach(l -> context.addLink(l));
    }
}