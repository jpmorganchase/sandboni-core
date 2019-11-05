package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.http.JavaxControllerMethodVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.http.SpringControllerMethodVisitor;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestSuiteVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotation;
import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotationParameter;
import static com.sandboni.core.engine.finder.bcel.visitors.Annotations.TEST.RUN_WITH_VALUE_SUITE;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CUCUMBER_RUNNER_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.TEST_SUITE_VERTEX;

/**
 * Visit Java Test classes.
 * Note: This class is not thread safe.
 */
public class TestClassVisitor extends ClassVisitorBase implements ClassVisitor {
    static final String JUNIT_PACKAGE = "org/junit/Test";
    static final String TESTING_PACKAGE = "org/testing/annotations/Test";

    private static final String CUCUMBER_RUNNER_CLASS = "cucumber/api/junit/Cucumber";
    private static final String RUN_WITH = "runWith";
    private static final String VALUE = "value";
    private static final String FEATURES = "features";

    private Set<String> testMethods = new HashSet<>();
    private Map<String, LinkType> initMethods = new HashMap<>();

    private boolean ignore;
    private boolean classIncluded;
    private boolean isSuite;

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
        AnnotationEntry runWithAnnotation = getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.TEST.RUN_WITH.getDesc());
        if (Objects.nonNull(runWithAnnotation)) {
            visitRunWithAnnotation(runWithAnnotation, jc);
            if(isSuite) return; // test methods are not executed for a suite class
        }

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

    private void visitRunWithAnnotation(AnnotationEntry runWithAnnotation, JavaClass jc) {
        String value = AnnotationUtils.getAnnotationParameter(runWithAnnotation, VALUE);
        if (CUCUMBER_RUNNER_CLASS.equals(value)) {
            TestVertex.Builder runnerBuilder = new TestVertex.Builder(jc.getClassName(), RUN_WITH, context.getCurrentLocation());
            AnnotationEntry cucumberOptionsAnnotation = getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.TEST.CUCUMBER_OPTIONS.getDesc());
            if (Objects.nonNull(cucumberOptionsAnnotation)) {
                String features = AnnotationUtils.getAnnotationParameter(cucumberOptionsAnnotation, FEATURES);
                runnerBuilder.withRunWithOptions(features);
            }

            context.addLink(LinkFactory.createInstance(context.getApplicationId(), runnerBuilder.build(), CUCUMBER_RUNNER_VERTEX, LinkType.CUCUMBER_RUNNER));
        }
        // if this is a test suite - we need to save the relations on the context, for late processing in the connector
        else if(RUN_WITH_VALUE_SUITE.getDesc().equals(value)) {
            AnnotationEntry suiteAnnotation = getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.TEST.SUITE_CLASSES.getDesc());
            if(suiteAnnotation != null) {
                this.isSuite = true;
                String classesList = getAnnotationParameter(suiteAnnotation, VALUE);
                List<String> testClasses = Arrays.stream(classesList.split(",")).map(s -> s.replace(File.separatorChar,'.')).collect(Collectors.toList());
                String suiteClassName = jc.getClassName();
                // add a link from TEST_SUITE_VERTEX to suite
                // note: test suite vertex has to be a TestVertex in order to be able to return it as one for the results for RelatedTestsOperation.execute()..
                TestSuiteVertex sv = new TestSuiteVertex.Builder(suiteClassName, "", context.getCurrentLocation()).build();
                context.addLink(LinkFactory.createInstance(context.getApplicationId(), TEST_SUITE_VERTEX, sv, LinkType.TEST_SUITE));
                // add links from suite to each test class
                testClasses.forEach(c -> {
                    Vertex cv = new Vertex.Builder(c, "", context.getCurrentLocation()).build();
                    context.addLink(LinkFactory.createInstance(context.getApplicationId(), sv, cv, LinkType.TEST_SUITE));
                });
            }
        }
    }
}