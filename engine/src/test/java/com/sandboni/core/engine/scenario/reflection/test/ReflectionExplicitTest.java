package com.sandboni.core.engine.scenario.reflection.test;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;

public class ReflectionExplicitTest {

    @Test
    public void testExplicitReflectionCall() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Integer integer = 1;
        Method explicitCallMethod = Integer.class.getDeclaredMethod("toString");
        explicitCallMethod.invoke(integer);
        assertTrue("test case created for reflection test", true);
    }

}
