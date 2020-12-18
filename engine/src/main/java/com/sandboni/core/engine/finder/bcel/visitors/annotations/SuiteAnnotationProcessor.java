package com.sandboni.core.engine.finder.bcel.visitors.annotations;

import com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils;
import com.sandboni.core.engine.finder.bcel.visitors.Annotations;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestSuiteVertex;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotationParameter;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.TEST_SUITE_VERTEX;

public class SuiteAnnotationProcessor implements RunWithAnnotationProcessor {
    private static final String VALUE = "value";

    @Override
    public boolean process(JavaClass jc, Context context) {
        Optional<AnnotationEntry> suiteAnnotation = AnnotationUtils.getAnnotation(jc.getConstantPool(), jc::getAnnotationEntries, Annotations.TEST.SUITE_CLASSES.getDesc());
        if(suiteAnnotation.isPresent()) {
            String classesList = getAnnotationParameter(suiteAnnotation.get(), VALUE);
            Set<String> testClasses = Arrays.stream(classesList.split(",")).map(s -> s.replace('/','.')).collect(Collectors.toSet());
            // note: test suite vertex has to be a TestVertex in order to be able to return it as one for the results for RelatedTestsOperation.execute()..
            TestSuiteVertex sv = new TestSuiteVertex.Builder(jc.getClassName(), testClasses, context.getCurrentLocation()).build();
            context.addLink(LinkFactory.createInstance(context.getApplicationId(), TEST_SUITE_VERTEX, sv, LinkType.TEST_SUITE));
            return false;
        }
        return true;
    }

}

