package com.sandboni.core.engine.finder.bcel.visitors.annotations;

import com.sandboni.core.engine.sta.Context;
import org.apache.bcel.classfile.JavaClass;

public interface RunWithAnnotationProcessor {

    /**
     *
     * @param jc
     * @param context
     * @return whether tests should be processed
     */
    boolean process(JavaClass jc, Context context);
}
