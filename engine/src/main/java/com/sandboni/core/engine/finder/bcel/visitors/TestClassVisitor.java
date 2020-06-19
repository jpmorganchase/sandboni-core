package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.annotations.RunWithAnnotationProcessorFactory;
import com.sandboni.core.engine.finder.bcel.visitors.http.JavaxControllerMethodVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.http.SpringControllerMethodVisitor;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.*;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotation;
import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotationParameter;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;

/**
 * Visit Java Test classes.
 * Note: This class is not thread safe.
 */
public class TestClassVisitor extends ClassVisitorBase implements ClassVisitor {
    private static final String JUNIT_PACKAGE = "org/junit/Test";
    private static final String TESTING_PACKAGE = "org/testing/annotations/Test";
    private static final String JUNIT_JUPITER_PACKAGE = "org/junit/jupiter/api/Test";
    static final String[] ALL_TEST_PACKAGES = {JUNIT_PACKAGE, TESTING_PACKAGE, JUNIT_JUPITER_PACKAGE};

    private static final String VALUE = "value";

    private Set<String> testMethods = new HashSet<>();
    private Map<String, LinkType> initMethods = new HashMap<>();

    private boolean ignore;
    private boolean alwaysRunClass;

    public void setUp() {
        ignore = false;
        testMethods = new HashSet<>();
        initMethods = new HashMap<>();
        initMethods.put(MethodUtils.INIT, LinkType.METHOD_CALL);
        initMethods.put(MethodUtils.CLINIT, LinkType.STATIC_CALL);
    }

    @Override
    public void visitMethod(Method method) {
        boolean testMethod = getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, ALL_TEST_PACKAGES) != null;
        if (testMethod) {
            new TestMethodVisitor(method, javaClass, context, ignore, alwaysRunClass).start();
            new TestHttpMethodVisitor(method, javaClass, context).start();
            new SpringControllerMethodVisitor(method, javaClass, context, false).start();
            new JavaxControllerMethodVisitor(method, javaClass, context, null, false).start();
            new SpringMockMvcMethodVisitor(method, javaClass, context).start();
            testMethods.add(formatMethod(method));
        }
        boolean initMethod = AnnotationUtils.isBefore(javaClass, method::getAnnotationEntries) ||
                AnnotationUtils.isAfter(javaClass, method::getAnnotationEntries);
        if (initMethod) {
            initMethods.put(formatMethod(method), method.isStatic() ? LinkType.STATIC_CALL : LinkType.METHOD_CALL);
        }
    }

    @Override
    public synchronized void visitJavaClass(JavaClass jc) {
        setUp();
        this.ignore = AnnotationUtils.isIgnore(jc, jc::getAnnotationEntries);
        AnnotationEntry runWithAnnotation = getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.TEST.RUN_WITH.getDesc(), Annotations.TEST.EXTEND_WITH.getDesc());
        if (Objects.nonNull(runWithAnnotation) && !visitRunWithAnnotation(runWithAnnotation, jc)) return;

        this.alwaysRunClass = isAlwaysRunAnnotation(jc);

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

    private boolean isAlwaysRunAnnotation(JavaClass jc) {
        boolean alwaysRunAnnotation = Objects.nonNull(getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, context.getAlwaysRunAnnotation()));
        if (alwaysRunAnnotation) {
            return true;
        }

        AnnotationEntry categoryAnnotation = getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.TEST.CATEGORY.getDesc());
        if (Objects.nonNull(categoryAnnotation)) {
            String categoryAnnotationValue = getAnnotationParameter(categoryAnnotation, VALUE);
            return Objects.nonNull(categoryAnnotationValue) && categoryAnnotationValue.endsWith(context.getAlwaysRunAnnotation());
        }

        return false;
    }

    /**
     *
     * @param runWithAnnotation
     * @param jc
     * @return a boolean indication weather we should continue processing the class
     */
    private boolean visitRunWithAnnotation(AnnotationEntry runWithAnnotation, JavaClass jc) {
        String value = AnnotationUtils.getAnnotationParameter(runWithAnnotation, VALUE);
        return new RunWithAnnotationProcessorFactory().getProcessor(value).process(jc, context);
    }
}