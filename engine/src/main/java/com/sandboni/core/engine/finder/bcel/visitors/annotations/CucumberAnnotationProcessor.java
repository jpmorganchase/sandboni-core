package com.sandboni.core.engine.finder.bcel.visitors.annotations;

import com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils;
import com.sandboni.core.engine.finder.bcel.visitors.Annotations;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;

import java.util.Objects;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotationParameter;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CUCUMBER_RUNNER_VERTEX;

public class CucumberAnnotationProcessor implements RunWithAnnotationProcessor {

    private static final String RUN_WITH = "runWith";
    private static final String FEATURES = "features";

    @Override
    public boolean process(JavaClass jc, Context context) {
        TestVertex.Builder runnerBuilder = new TestVertex.Builder(jc.getClassName(), RUN_WITH, context.getCurrentLocation());
        AnnotationEntry cucumberOptionsAnnotation = AnnotationUtils.getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.TEST.CUCUMBER_OPTIONS.getDesc());
        if (Objects.nonNull(cucumberOptionsAnnotation)) {
            String features = getAnnotationParameter(cucumberOptionsAnnotation, FEATURES);
            runnerBuilder.withRunWithOptions(features);
        }

        context.addLink(LinkFactory.createInstance(context.getApplicationId(), runnerBuilder.build(), CUCUMBER_RUNNER_VERTEX, LinkType.CUCUMBER_RUNNER));
        return true;
    }
}
