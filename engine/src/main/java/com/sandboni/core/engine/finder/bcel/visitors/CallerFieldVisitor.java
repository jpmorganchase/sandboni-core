package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.PUTSTATIC;

public class CallerFieldVisitor extends CallerFieldOrMethodVisitor {

    CallerFieldVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
    }

    @Override
    public void visitPUTFIELD(PUTFIELD i) {
        addLink(LinkFactory.createInstance(
                new Vertex.Builder(i.getReferenceType(cp).toString(), i.getName(cp)).build(),
                currentMethodVertex,
                LinkType.FIELD_PUT));
    }

    @Override
    public void visitGETFIELD(GETFIELD i) {
        addLink(LinkFactory.createInstance(currentMethodVertex,
                new Vertex.Builder(i.getReferenceType(cp).toString(), i.getName(cp)).build(),
                LinkType.FIELD_GET));
    }

    @Override
    public void visitPUTSTATIC(PUTSTATIC i) {
        addLink(LinkFactory.createInstance(
                new Vertex.Builder(i.getReferenceType(cp).toString(), i.getName(cp)).build(),
                currentMethodVertex,
                LinkType.STATIC_PUT));
    }

    @Override
    public void visitGETSTATIC(GETSTATIC i) {
        addLink(LinkFactory.createInstance(currentMethodVertex,
                new Vertex.Builder(i.getReferenceType(cp).toString(), i.getName(cp)).build(),
                LinkType.STATIC_GET));
    }
}