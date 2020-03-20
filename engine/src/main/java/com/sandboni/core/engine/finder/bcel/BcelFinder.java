package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.finder.ExtensionType;
import com.sandboni.core.engine.finder.FileTreeFinder;
import com.sandboni.core.engine.finder.bcel.visitors.*;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

public class BcelFinder extends FileTreeFinder {

    public BcelFinder() {
        // To support parallel execution, now Visitors are not longer supported as constructor parameters,
        // instead these are created on demand when the startVisitors method is called, this ensure that
        // a different instance is used across multiple Finder executions.
    }

    @Override
    protected Map<String, ThrowingBiConsumer<File, Context>> getConsumers() {
        HashMap<String, ThrowingBiConsumer<File, Context>> map = new HashMap<>();
        map.put(ExtensionType.CLASS.type(), (file, context) -> {
            ClassParser cp = new ClassParser(file.getAbsolutePath());
            JavaClass jc = cp.parse();
            jc.setRepository(ClassUtils.getRepository(context.getClassPath()));
            Context localContext = context.getLocalContext();
            context.addLinks(startVisitors(jc, localContext));
        });

        return map;
    }

    protected Link[] startVisitors(JavaClass jc, Context c) {
        return Arrays.stream(ClassVisitors.getClassVisitors()).flatMap(v -> {
            Context localContext = c.getLocalContext();
            Stream<Link> start = v.start(jc, localContext);
            c.addLinks(localContext.getLinks());
            return start;
        }).toArray(Link[]::new);
    }

}
