package com.sandboni.core.engine.scenario.lambda;

import java.util.function.Function;

public interface SpecificsConverter<T extends ActivityBlotterRecord> extends Function<String, T> {
}
