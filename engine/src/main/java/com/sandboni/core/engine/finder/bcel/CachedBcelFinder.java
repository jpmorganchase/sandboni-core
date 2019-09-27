package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import org.apache.bcel.classfile.JavaClass;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedBcelFinder extends BcelFinder {

    private ConcurrentMap<JavaClass, Link[]> linkCache = new ConcurrentHashMap<>();

    public CachedBcelFinder(ClassVisitor[] visitors) {
        super(visitors);
    }

    @Override
    protected Link[] startVisitors(JavaClass jc, Context c) {
        return linkCache.computeIfAbsent(jc, jClass -> super.startVisitors(jClass, c));
    }

}
