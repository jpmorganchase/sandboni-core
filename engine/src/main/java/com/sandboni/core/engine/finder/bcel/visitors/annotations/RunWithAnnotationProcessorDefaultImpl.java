package com.sandboni.core.engine.finder.bcel.visitors.annotations;

import com.sandboni.core.engine.sta.Context;
import org.apache.bcel.classfile.JavaClass;

public class RunWithAnnotationProcessorDefaultImpl implements RunWithAnnotationProcessor {

    @Override
    public void process(JavaClass jc, Context context) {
        //do nothing
    }

    @Override
    public boolean isSuite() {
        return false;
    }
}
