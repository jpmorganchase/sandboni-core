package com.sandboni.core.scenario.concurrent;

public class InterfaceToTestImpl implements InterfaceToTest {

    @Override
    public String methodToImplementAndChange() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append('a');
        }
        return sb.toString();
    }
}
