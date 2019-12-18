package com.sandboni.core.engine.scenario.lambda;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class RecordConverter<A extends ActivityBlotterRecord> implements Function<BlotterInput, String> {

    private final SpecificsConverter<A> specifics;

    public RecordConverter(SpecificsConverter<A> specifics) {
        this.specifics = specifics;
    }

    @Override
    public String apply(BlotterInput blotterInput) {
        populateField(Object::toString, () -> {
            return (ActivityBlotterRecord)this.specifics.apply(blotterInput);
        });
        return blotterInput.toString();
    }

    private static <T> void populateField(Consumer<? super T> fieldConsumer, Supplier<? extends T> valueSupplier) {
        Try.to(valueSupplier);
    }
}
