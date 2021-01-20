package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.cucumber.CucumberFeatureFinder;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Optional;

import static com.sandboni.core.engine.finder.bcel.visitors.AnnotationUtils.getAnnotationParameter;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CUCUMBER_VERTEX;

public class CucumberMethodVisitor extends CallerFieldOrMethodVisitor {
    CucumberMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
    }

    @Override
    public void visitInstructions(Method method) {
        Optional<AnnotationEntry> annotation = AnnotationUtils.getAnnotation(javaClass.getConstantPool(), method::getAnnotationEntries, CucumberFeatureFinder.getCucumberSteps().toArray(new String[0]));
        if (annotation.isPresent()) {
            String cucumberText = getAnnotationParameter(annotation.get(), "value");
            if (cucumberText != null && !cucumberText.isEmpty()) {
                super.addLink(LinkFactory.createInstance(
                        context.getApplicationId(),
                        new Vertex.Builder(CUCUMBER_VERTEX.getActor(), cucumberText, context.getCurrentLocation()).build(),
                        new Vertex.Builder(javaClass.getClassName(), formatMethod(method)).build(),
                        LinkType.CUCUMBER_SOURCE));
            }
        }
    }
}
