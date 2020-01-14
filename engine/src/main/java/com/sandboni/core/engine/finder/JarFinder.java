package com.sandboni.core.engine.finder;

import com.sandboni.core.engine.ProcessorBuilder;
import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.contract.ThrowingFunction;
import com.sandboni.core.engine.finder.cucumber.CucumberFeatureFinder;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.utils.TimeUtils;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFinder extends FileTreeFinder {
    private static final Logger log = LoggerFactory.getLogger(JarFinder.class);

    @Override
    protected Map<String, ThrowingBiConsumer<File, Context>> getConsumers() {
        ThrowingFunction<ClassParser, JavaClass> parse = ClassParser::parse;
        HashMap<String, ThrowingBiConsumer<File, Context>> map = new HashMap<>();
        map.put(ExtensionType.JAR.type(), (file, context) -> {
            log.info("[{}] Jar scanning starts for {}", Thread.currentThread().getName(), file);
            long start = System.nanoTime();
            try (JarFile jar = new JarFile(file)) {
                Collections.list(jar.entries()).stream()
                        .filter(e -> !e.isDirectory())
                        .forEach(e -> {
                            if (e.getName().endsWith(ExtensionType.FEATURE.type())) {
                                List<Link> links = CucumberFeatureFinder.toLinks(context, new File(file.getAbsolutePath() + File.separator + e.getName()), getFileContent(jar, e));
                                context.addLinks(links.toArray(new Link[0]));
                            } else if (e.getName().endsWith(ExtensionType.CLASS.type()) && context.inScope(e.getName())) {
                                ClassParser classParser = new ClassParser(file.getAbsolutePath(), e.getName());
                                JavaClass jc = parse.apply(classParser);
                                Link[] links = startVisitors(jc, context);
                                context.addLinks(links);
                            }
                        });
            }
            log.info("[{}] Jar scanning finished for {} in {} milliseconds", Thread.currentThread().getName(), file, TimeUtils.elapsedTime(start));
        });
        return map;
    }

    @Override
    protected boolean scanDependencies() {
        return true;
    }

    private Link[] startVisitors(JavaClass jc, Context c) {
        return Arrays.stream(ProcessorBuilder.getVisitors()).flatMap(v -> v.start(jc, c)).toArray(Link[]::new);
    }

    private String getFileContent(JarFile jar, JarEntry entry) {
        try {
            InputStream inputStream = jar.getInputStream(entry);
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Error during reading file: " + entry.getName(), e);
        }
        return null;
    }
}
