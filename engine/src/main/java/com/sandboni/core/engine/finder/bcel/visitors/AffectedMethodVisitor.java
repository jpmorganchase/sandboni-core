package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;

class AffectedMethodVisitor extends MethodVisitorBase {

    private Set<Integer> changes;

    AffectedMethodVisitor(Method method, JavaClass jc, Context c, Set<Integer> changes) {
        super(method, jc, c);
        this.changes = Collections.unmodifiableSet(changes);
    }

    private void addEntryLink() {
        addLink(LinkFactory.createInstance(
                new Vertex.Builder(javaClass.getClassName(), MethodUtils.formatMethod(method)).build(),
                VertexInitTypes.END_VERTEX,
                LinkType.EXIT_POINT));
    }

    void start() {
        // line numbers can be out of order (as in case of field initializations)
        // for now-empty line changes inside the method we must use range
        // this does NOT cover changes made to multi-line field initializations

        List<Integer> lines = MethodUtils.getMethodLineNumbers(method);
        for (Integer ln : lines) {
            if (changes.contains(ln)) {
                addEntryLink();
                break;
            }
        }

        if (linksCount == 0 && !lines.isEmpty()) {
            int firstLine = lines.get(0);
            int lastLine = lines.get(lines.size() - 1);
            if (changes.stream().anyMatch(c -> c >= firstLine && c <= lastLine)) {
                addEntryLink();
            }
        }
    }
}

