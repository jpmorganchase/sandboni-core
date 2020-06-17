package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Method;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.DEAD_END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.REFLECTION_CALL_VERTEX;

/**
 * Visit Java classes that contains method calls to other class members.
 * Note: This class is not thread safe.
 */
public class CallerClassVisitor extends ClassVisitorBase implements ClassVisitor {

    private static final String ACTION_REFLECTION_REF_TEST = "reflection ref (test)";
    private static final String ACTION_REFLECTION_REF = "reflection ref";

    private static final List<ReflectionSearchPatters> reflectionClassSearchPatters = Arrays.asList(ReflectionSearchPatters.values());

    enum ReflectionSearchPatters {
        JAVA_LANG_REFLECT("java/lang/reflect/"),
        POWER_MOCK_REFLECT("org/powermock/reflect/"),
        SPRING_REFLECTION_UTILS("org/springframework/util/ReflectionUtils"),
        SPRING_REFLECTION_TEST_UTILS("org/springframework/test/util/ReflectionTestUtilseMapping"),
        SPOCK_REFLECTION_UTIL("org/spockframework/util/ReflectionUtil"),
        ASSERT_J_CORE_EXTRACTORS("org/assertj/core/extractor/Extractors");

        private String desc;

        ReflectionSearchPatters(String desc) {
            this.desc = desc;
        }

        String getDesc() {
            return desc;
        }

        boolean isContainedIn(String className) {
            return className.contains(getDesc());
        }
    }

    private CallerFieldOrMethodVisitor[] getVisitors(Method m) {
        return new CallerFieldOrMethodVisitor[]{
            new CallerMethodVisitor(m, javaClass, context),
            new CallerFieldVisitor(m, javaClass, context),
            new AnonymousClassMethodVisitor(m, javaClass, context),
            new ThreadRunMethodVisitor(m, javaClass, context),
            new CucumberMethodVisitor(m, javaClass, context)};
    }

    @Override
    public void visitMethod(Method method) {
        long linksAdded = Arrays.stream(getVisitors(method))
            .map(CallerFieldOrMethodVisitor::start)
            .mapToInt(i -> i).sum();

        // in order to detect locations (modules) each method has to have at least one entry in the graph as caller
        if (linksAdded == 0) {
            Vertex v;
            if (isTestLocation()) {
                v = new TestVertex.Builder(this.javaClass.getClassName(), formatMethod(method), context.getCurrentLocation()).build();
            } else {
                v = new Vertex.Builder(this.javaClass.getClassName(), formatMethod(method), context.getCurrentLocation())
                    .build();
            }
            context.addLink(LinkFactory.createInstance(context.getApplicationId(), v, DEAD_END_VERTEX, LinkType.METHOD_CALL));
        }
    }

    private boolean isTestLocation() {
        return Objects.nonNull(context.getCurrentLocation()) && context.getTestLocations().contains(context.getCurrentLocation());
    }

    @Override
    public void visitConstantPool(final ConstantPool obj) {
        if (foundReflectionInMethodCalls(obj)) {
            linkToReflectionCallVertex();
        }
    }

    private boolean foundReflectionInMethodCalls(ConstantPool obj) {
        return Arrays.asList(obj.getConstantPool()).parallelStream().filter(Objects::nonNull).filter(c -> c.getTag() == Const.CONSTANT_Methodref).
            anyMatch(c -> {
                String invokedClassName = ClassUtils.getClassNameFromMethodCall(obj, (ConstantMethodref) c);
                return reflectionClassSearchPatters.stream().anyMatch(p -> p.isContainedIn(invokedClassName));
            });
    }

    private void linkToReflectionCallVertex() {
        Vertex v;
        LinkType linkType;
        if (isTestLocation()) {
            v = new TestVertex.Builder(this.javaClass.getClassName(), ACTION_REFLECTION_REF_TEST, context.getCurrentLocation()).build();
            linkType = LinkType.REFLECTION_CALL_TEST;
        } else {
            v = new Vertex.Builder(this.javaClass.getClassName(), ACTION_REFLECTION_REF, context.getCurrentLocation()).build();
            linkType = LinkType.REFLECTION_CALL_SRC;
        }
        context.addLink(LinkFactory.createInstance(context.getApplicationId(), v, REFLECTION_CALL_VERTEX, linkType));
    }

}