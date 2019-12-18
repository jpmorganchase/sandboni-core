package com.sandboni.core.engine.scenario.lambda;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class RecordConverter<A extends ActivityBlotterRecord> implements Function<Integer, String> {

    private final SpecificsConverter<A> specifics;

    public RecordConverter(SpecificsConverter<A> specifics) {
        this.specifics = specifics;
    }

    @Override
    public String apply(Integer integer) {
        populateField(Object::toString, () -> {
            return (ActivityBlotterRecord)this.specifics.apply("input");
        });
        return integer.toString();
    }

    private static <T> void populateField(Consumer<? super T> fieldConsumer, Supplier<? extends T> valueSupplier) {
        Try.to(valueSupplier);
    }
}
