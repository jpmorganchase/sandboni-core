package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Visitor;

import java.util.stream.Stream;

public interface ClassVisitor extends Visitor {
    Stream<Link> start(JavaClass javaClass, Context context);
}
