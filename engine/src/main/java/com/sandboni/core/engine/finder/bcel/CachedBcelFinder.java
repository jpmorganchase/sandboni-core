package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import org.apache.bcel.classfile.JavaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedBcelFinder extends BcelFinder {
    private static final Logger logger = LoggerFactory.getLogger(CachedBcelFinder.class);

    private static ConcurrentMap<JavaClass, Link[]> linkCache = new ConcurrentHashMap<>();

    public CachedBcelFinder(ClassVisitor[] visitors) {
        super(visitors);
    }

    public static void clearLinkCache() {
        logger.debug("Clearing Bcel Finder cache");
        linkCache.clear();
    }

    @Override
    protected Link[] startVisitors(JavaClass jc, Context c) {
        return linkCache.computeIfAbsent(jc, jClass -> super.startVisitors(jClass, c));
    }
}
