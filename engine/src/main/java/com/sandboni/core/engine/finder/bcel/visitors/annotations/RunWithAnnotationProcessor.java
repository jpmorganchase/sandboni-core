package com.sandboni.core.engine.finder.bcel.visitors.annotations;

import com.sandboni.core.engine.sta.Context;
import org.apache.bcel.classfile.JavaClass;

public interface RunWithAnnotationProcessor {

    void process(JavaClass jc, Context context);

    boolean isSuite();
}
