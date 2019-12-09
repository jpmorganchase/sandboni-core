package com.sandboni.core.engine.scenario;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category(AlwaysRun.class)
public class AlwaysRunCategoryClassTest {
    @Test
    public void testOne() {
        assertTrue(true);
    }
}
