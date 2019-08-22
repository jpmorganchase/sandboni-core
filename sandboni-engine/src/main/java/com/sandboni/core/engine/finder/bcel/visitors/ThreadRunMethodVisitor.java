package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKEVIRTUAL;

public class ThreadRunMethodVisitor extends CallerFieldOrMethodVisitor {

    private static final String THREAD_CLASS_NAME = "java.lang.Thread";
    private static final String THREAD_START_METHOD = "start";

    private static final String RUNNABLE_INTERFACE_METHOD = "java.lang.Runnable";
    private static final String RUNNABLE_RUN_METHOD = "run()";

    private static final String ES_INTERFACE_NAME = "java.util.concurrent.ExecutorService";
    private static final String ES_EXECUTE_METHOD = "execute";
    private static final String ES_SUBMIT_METHOD  = "submit";



    ThreadRunMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
        if (THREAD_CLASS_NAME.equals(i.getReferenceType(cp).toString()) && THREAD_START_METHOD.equals(i.getMethodName(cp))) {
            addLink(LinkFactory.createInstance(currentMethodVertex, new Vertex.Builder(RUNNABLE_INTERFACE_METHOD, RUNNABLE_RUN_METHOD, context.getCurrentLocation()).build(), LinkType.INTERFACE_CALL));
        }
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE obj) {
        if(ES_INTERFACE_NAME.equals(obj.getReferenceType(cp).toString()) &&
                (ES_EXECUTE_METHOD.equals(obj.getMethodName(cp)) || ES_SUBMIT_METHOD.equals(obj.getMethodName(cp))))
            addLink(LinkFactory.createInstance(currentMethodVertex, new Vertex.Builder(RUNNABLE_INTERFACE_METHOD, RUNNABLE_RUN_METHOD, context.getCurrentLocation()).build(), LinkType.INTERFACE_CALL));
    }

}