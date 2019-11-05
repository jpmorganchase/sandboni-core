package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.finder.ExtensionType;
import com.sandboni.core.engine.finder.FileTreeFinder;
import com.sandboni.core.engine.finder.bcel.visitors.ClassUtils;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class BcelFinder extends FileTreeFinder {

    private Collection<ClassVisitor> visitors;
    private static final Logger log = LoggerFactory.getLogger(BcelFinder.class);

    public BcelFinder(ClassVisitor[] visitors) {
        this.visitors = Collections.unmodifiableCollection(Arrays.asList(visitors));
    }

    @Override
    protected Map<String, ThrowingBiConsumer<File, Context>> getConsumers() {
        HashMap<String, ThrowingBiConsumer<File, Context>> map = new HashMap<>();
        map.put(ExtensionType.CLASS.type(), (file, context) -> {
            ClassParser cp = new ClassParser(file.getAbsolutePath());
            if(file.getName().contains("SuiteTestClass")) log.info(String.format("Creating JavaClass for: %s; ", file.getName()));
            JavaClass jc = cp.parse();
            jc.setRepository(ClassUtils.getRepository(context.getClassPath()));
            context.addLinks(startVisitors(jc, context));
        });

        return map;
    }

    protected Link[] startVisitors(JavaClass jc, Context c) {
        return visitors.stream().flatMap(v -> v.start(jc, c)).toArray(Link[]::new);
    }
}
