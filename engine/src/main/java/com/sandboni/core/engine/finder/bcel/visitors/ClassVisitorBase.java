package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.scm.utils.timing.SWConsts;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
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
        StopWatch sw1 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_VISIT_JAVA_CLASS, "constantPool").start();
        jc.getConstantPool().accept(this);
        sw1.stop();
        StopWatch sw2 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_VISIT_JAVA_CLASS, "visitMethods").start();
        for (Method method : jc.getMethods()) {
            method.accept(this);
        }
        sw2.stop();
    }

    public Stream<Link> start(JavaClass jc, Context c) {
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_START, "ALL").start();
        StopWatch sw1 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_START, "init").start();
        javaClass = jc;
        context = c.getLocalContext();
        constantPoolGen = new ConstantPoolGen(javaClass.getConstantPool());
        sw1.stop();
        StopWatch sw2 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_START, SWConsts.METHOD_NAME_VISIT_JAVA_CLASS).start();
        visitJavaClass(javaClass);
        sw2.stop();
        Stream<Link> links = context.getLinks();
        swAll.stop();
        return links;
    }
}