package com.sandboni.core.engine.scenario.reflection.test;

import com.sandboni.core.engine.contract.ThrowingConsumer;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ReflectionLambdaTest {

    private void invokeExplicitCall(Integer integer) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method explicitCallMethod = Integer.class.getDeclaredMethod("toString");
        explicitCallMethod.invoke(integer);
    }

    @Test
    public void testExplicitReflectionInLambda() {
        List<Integer> someIntList = Arrays.asList(1, 2);
        ThrowingConsumer<Integer> consumer = this::invokeExplicitCall;
        someIntList.forEach(consumer);
        assertTrue("test case created for reflection test", true);
    }
}
