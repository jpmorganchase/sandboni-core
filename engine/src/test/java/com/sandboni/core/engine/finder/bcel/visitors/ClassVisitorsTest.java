package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class ClassVisitorsTest {

    @Test
    public void testDifferentInstance() {
        ClassVisitor[] classVisitorsFirst = ClassVisitors.getClassVisitors();
        ClassVisitor[] classVisitorsSecond = ClassVisitors.getClassVisitors();

        Arrays.stream(classVisitorsFirst)
                .forEach(first ->
                        Arrays.stream(classVisitorsSecond)
                                .filter(second -> first.getClass().equals(second.getClass()))
                                .findAny()
                                .ifPresent(second -> Assert.assertNotSame(first, second))
                );
    }
}
