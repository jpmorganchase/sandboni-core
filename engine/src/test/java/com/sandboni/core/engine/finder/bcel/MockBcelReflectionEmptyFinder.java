package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.sta.Finder;
import com.sandboni.core.engine.sta.Context;

import java.io.IOException;

public class MockBcelReflectionEmptyFinder implements Finder {
    @Override
    public void find(Context context) throws IOException {
        // do nothing
    }
}
