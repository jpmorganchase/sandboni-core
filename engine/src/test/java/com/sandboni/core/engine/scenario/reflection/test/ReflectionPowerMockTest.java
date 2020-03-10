package com.sandboni.core.engine.scenario.reflection.test;

import com.sandboni.core.engine.scenario.reflection.src.ReflectionCaller;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertTrue;

public class ReflectionPowerMockTest {

    @Test
    public void testReflectionCallUsingPowerMock() throws Exception {
        ReflectionCaller reflectionCaller = new ReflectionCaller();
        Whitebox.invokeMethod(reflectionCaller, "reflectionCall");
        assertTrue("test case created for reflection test", true);
    }
}
