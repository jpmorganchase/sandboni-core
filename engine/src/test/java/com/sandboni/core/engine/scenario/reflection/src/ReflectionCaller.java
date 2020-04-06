package com.sandboni.core.engine.scenario.reflection.src;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionCaller {

    private void reflectionCall() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Integer integer = 1;
        Method explicitCallMethod = Integer.class.getDeclaredMethod("toString");
        explicitCallMethod.invoke(integer);
    }

    public void doSomething() {
        int i = 3;
    }


}
