package com.sandboni.core.engine;

import java.util.function.Consumer;

public interface BuilderPattern <R, T extends BuilderPattern>{

    T with(Consumer<T> builderFunction);

    R build();
}
