package com.sandboni.core.engine.scenario.lambda;

import java.util.List;

public class DeltaOneBlotterConverter implements SpecificsConverter<DeltaOneBlotterRecord> {

    private List<String> listOfBlotterEnrichers;

    public DeltaOneBlotterConverter(List<String> listOfBlotterEnrichers) {
        this.listOfBlotterEnrichers = listOfBlotterEnrichers;
    }

    @Override
    public DeltaOneBlotterRecord apply(String blotterInput) {
        int i = 0;
        System.out.println("Calling the apply method in Function impl");
        return new DeltaOneBlotterRecord();
    }
}
