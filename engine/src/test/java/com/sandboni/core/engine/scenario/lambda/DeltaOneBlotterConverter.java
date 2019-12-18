package com.sandboni.core.engine.scenario.lambda;

import java.util.List;

public class DeltaOneBlotterConverter implements SpecificsConverter<DeltaOneBlotterRecord> {

    private List<String> listOfBlotterEnrichers;

    public DeltaOneBlotterConverter(List<String> listOfBlotterEnrichers) {
        this.listOfBlotterEnrichers = listOfBlotterEnrichers;
    }

    @Override
    public DeltaOneBlotterRecord apply(BlotterInput blotterInput) {
        System.out.println("Calling the apply method in Function impl");
        return new DeltaOneBlotterRecord();
    }
}
