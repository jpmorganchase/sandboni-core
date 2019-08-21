package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Visit Java classes that were modified in the change scope.
 * Note: This class is not thread safe.
 */
public class AffectedClassVisitor extends ClassVisitorBase {
    private Set<Integer> linesChanges;

    @Override
    public void visitMethod(Method method) {
        new AffectedMethodVisitor(method, javaClass, context, linesChanges).start();
    }

    @Override
    public Stream<Link> start(JavaClass jc, Context c) {
        String relativeFileName = MethodUtils.getRelativeFileName(jc);
        this.linesChanges = c.getChangeScope().getAllLinesChanged(relativeFileName);
        if (!linesChanges.isEmpty()) {
            return super.start(jc, c);
        }
        return Stream.empty();
    }
}