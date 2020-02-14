package com.sandboni.core.engine.filter;

import com.sandboni.core.engine.MockChangeDetector;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

public class ChangeScopeFilterTest {

    private ChangeScope<Change> changeScope;
    private ChangeScopeFilter filter;

    @Before
    public void setUp() {
        changeScope = new MockChangeDetector().getChanges("1", "2");
        filter = new ChangeScopeFilter();
    }

    @Test
    public void isInScope() {
        Assert.assertTrue(filter.isInScope(changeScope, Collections.singleton(new File("./target/test-classes"))));
    }

    @Test
    public void notInScope() {
        Assert.assertFalse(filter.isInScope(changeScope, Collections.singleton(new File("./non-existing-dir"))));
    }
}
