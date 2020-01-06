package com.sandboni.core.engine.scenario.tests;

import com.sandboni.core.engine.scenario.lambda.BlotterInput;
import com.sandboni.core.engine.scenario.lambda.DeltaOneBlotterConverter;
import com.sandboni.core.engine.scenario.lambda.DeltaOneBlotterRecord;
import com.sandboni.core.engine.scenario.lambda.RecordConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DeltaOneBlotterConverterTest {
    private RecordConverter<DeltaOneBlotterRecord> converter;

    @Before
    public void setUp() {
        List<String> listOfBlotterEnrichers = new ArrayList<>();
        this.converter = new RecordConverter<>(new DeltaOneBlotterConverter(listOfBlotterEnrichers));
    }

    @Test
    public void applyTests() {
        Assert.assertNotNull(converter.apply(new BlotterInput()));
    }

}
