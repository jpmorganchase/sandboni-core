package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.bcel.visitors.http.JavaxControllerClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.http.SpringControllerClassVisitor;

public class ClassVisitors {

    /**
     * Utility method that always return new ClassVisitors instances.
     * This is needed as Visitors are not thread safe and a single instance
     * can't be shared across multiple threads.
     *
     * @return ClassVisitor array with new instances in each invocation.
     */
    public static ClassVisitor[] getClassVisitors() {
        return new ClassVisitor[]{
                new AffectedClassVisitor(),
                new CallerClassVisitor(),
                new ImplementingClassVisitor(),
                new InheritanceClassVisitor(),
                new JavaxControllerClassVisitor(),
                new SpringControllerClassVisitor(),
                new TestClassVisitor()};
    }

    private ClassVisitors() {
        // static methods
    }
}
