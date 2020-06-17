package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;

import java.util.stream.Stream;

/**
 * Base class for all the Class visitor implementations.
 * Note: This class is not thread safe.
 */
public abstract class ClassVisitorBase extends EmptyVisitor implements ClassVisitor {

    protected JavaClass javaClass;
    protected Context context;
    protected ConstantPoolGen constantPoolGen;

    @Override
    public void visitJavaClass(JavaClass jc) {
        jc.getConstantPool().accept(this);
        for (Method method : jc.getMethods()) {
            method.accept(this);
        }
    }

    public Stream<Link> start(JavaClass jc, Context c) {
        javaClass = jc;
        context = c.getLocalContext();
        constantPoolGen = new ConstantPoolGen(javaClass.getConstantPool());
        visitJavaClass(javaClass);
        return context.getLinks();
    }
}