package com.sandboni.core.engine.scenario.lambda;

import java.util.function.Supplier;

public class Try {

    public static <T> T to(Supplier<? extends T> valueSupplier) {
        return valueSupplier.get();
    }
}
